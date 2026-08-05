from fastapi import APIRouter, Header, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
import hmac
import hashlib
import urllib.parse
import json
from datetime import datetime, timedelta, timezone
from jose import jwt

from app.core.config import settings
from app.core.database import get_db
from app.db.models.user import User
from app.db.models.user_profile import UserProfile
from app.api.deps import get_current_user

router = APIRouter()


def verify_telegram_init_data(init_data: str, bot_token: str) -> dict:
    """Валидация initData от Telegram через HMAC-SHA256"""
    try:
        parsed = dict(urllib.parse.parse_qsl(init_data))
        received_hash = parsed.pop('hash', None)

        if not received_hash:
            raise HTTPException(status_code=401, detail="No hash in init data")

        data_check_arr = [f"{k}={v}" for k, v in sorted(parsed.items())]
        data_check_string = "\n".join(data_check_arr)

        secret_key = hmac.new(b"WebAppData", bot_token.encode(), hashlib.sha256).digest()
        computed_hash = hmac.new(secret_key, data_check_string.encode(), hashlib.sha256).hexdigest()

        if not hmac.compare_digest(computed_hash, received_hash):
            raise HTTPException(status_code=401, detail="Invalid init data signature")

        auth_date = int(parsed.get('auth_date', 0))
        if datetime.now(timezone.utc).timestamp() - auth_date > 86400:
            raise HTTPException(status_code=401, detail="Init data expired")

        return parsed
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Validation failed: {str(e)}")


@router.post("/telegram-login")
async def telegram_login(
    x_telegram_init_data: str = Header(...),
    db: AsyncSession = Depends(get_db)
):
    parsed = verify_telegram_init_data(x_telegram_init_data, settings.BOT_TOKEN)

    user_data = json.loads(parsed.get('user', '{}'))
    telegram_id = user_data.get('id')
    username = user_data.get('username', '')
    first_name = user_data.get('first_name', '')
    last_name = user_data.get('last_name', '')

    if not telegram_id:
        raise HTTPException(status_code=400, detail="No user ID in init data")

    result = await db.execute(select(User).where(User.telegram_id == telegram_id))
    user = result.scalar_one_or_none()

    if not user:
        # Создаём нового пользователя с telegram_id
        user = User(
            telegram_id=telegram_id,
            username=username,
            first_name=first_name,
            last_name=last_name,
            is_active=True
        )
        db.add(user)
        try:
            await db.commit()
        except IntegrityError:
            # EDGE-CASE: два одновременных запроса могли создать одного и того же
            # пользователя (например, повторный вызов авто-входа) — берём уже созданного
            await db.rollback()
            result = await db.execute(select(User).where(User.telegram_id == telegram_id))
            user = result.scalar_one()
        else:
            await db.refresh(user)

            # Создаём профиль по умолчанию
            profile = UserProfile(
                user_id=user.id,
                gender="male",
                age=25,
                weight_kg=70.0,
                height_cm=175,
                activity_level="moderate",
                fitness_goal="maintain",
                calculation_formula="mifflin_st_jeor",
                calorie_adjustment=0
            )
            db.add(profile)
            await db.commit()
    else:
        # EDGE-CASE: обновляем данные из Telegram при каждом входе
        # Если пользователь сменил имя/username в Telegram — обновляем у нас
        user.username = username or user.username
        user.first_name = first_name or user.first_name
        user.last_name = last_name or user.last_name
        await db.commit()
        await db.refresh(user)

    token_data = {
        "sub": str(user.id),
        "telegram_id": user.telegram_id,
        "exp": datetime.utcnow() + timedelta(days=30)
    }
    token = jwt.encode(token_data, settings.SECRET_KEY, algorithm="HS256")

    return {
        "access_token": token,
        "token_type": "bearer",
        "user": {
            "id": user.id,
            "email": user.email,
            "telegram_id": user.telegram_id,
            "username": user.username,
            "first_name": user.first_name,
            "last_name": user.last_name
        }
    }
    
    
@router.post("/bind-telegram")
async def bind_telegram(
    x_telegram_init_data: str = Header(...),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Привязывает Telegram к уже авторизованному пользователю (email/password)"""
    parsed = verify_telegram_init_data(x_telegram_init_data, settings.BOT_TOKEN)

    user_data = json.loads(parsed.get('user', '{}'))
    telegram_id = user_data.get('id')

    if not telegram_id:
        raise HTTPException(status_code=400, detail="No user ID in init data")

    # Проверяем, не занят ли этот telegram_id другим пользователем
    existing = await db.execute(
        select(User).where(User.telegram_id == telegram_id, User.id != current_user.id)
    )
    if existing.scalar_one_or_none():
        raise HTTPException(status_code=409, detail="Этот Telegram уже привязан к другому аккаунту")

    # Привязываем и обновляем имя
    current_user.telegram_id = telegram_id
    current_user.first_name = user_data.get('first_name', current_user.first_name)
    current_user.last_name = user_data.get('last_name', current_user.last_name)
    current_user.username = user_data.get('username', current_user.username)

    await db.commit()
    await db.refresh(current_user)

    return {
        "detail": "Telegram успешно привязан",
        "telegram_id": telegram_id,
        "username": current_user.username
    }
    
@router.post("/unbind-telegram")
async def unbind_telegram(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Отвязывает Telegram от текущего пользователя"""
    if not current_user.telegram_id:
        raise HTTPException(status_code=400, detail="Telegram не привязан")
    
    current_user.telegram_id = None
    await db.commit()
    await db.refresh(current_user)
    
    return {"detail": "Telegram успешно отвязан"}