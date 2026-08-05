import pytest
import hmac
import hashlib
import json
import time
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.core.config import settings
from app.db.models.user import User


def make_valid_init_data(user_data: dict, bot_token: str = settings.BOT_TOKEN, expired: bool = False) -> str:
    """Генерирует валидный initData с правильной HMAC-подписью."""
    auth_date = int(time.time()) - (90000 if expired else 0)  # expired = >25 часов назад
    
    user_json = json.dumps(user_data, separators=(',', ':'))
    
    data_dict = {
        "auth_date": str(auth_date),
        "query_id": "AAHdF6IQAAAAAN0XohDhrOrc",
        "user": user_json,
        "hash": ""  # заполнится ниже
    }
    
    # Генерируем подпись по алгоритму Telegram
    data_check_arr = sorted([f"{k}={v}" for k, v in data_dict.items() if k != "hash"])
    data_check_string = "\n".join(data_check_arr)
    
    secret_key = hmac.new(b"WebAppData", bot_token.encode(), hashlib.sha256).digest()
    computed_hash = hmac.new(secret_key, data_check_string.encode(), hashlib.sha256).hexdigest()
    
    data_dict["hash"] = computed_hash
    
    # URL-encode строка
    return "&".join(f"{k}={v}" for k, v in data_dict.items())


@pytest.mark.asyncio
async def test_telegram_login_without_init_data(client: AsyncClient):
    """POST /api/auth/telegram-login без header → 422."""
    resp = await client.post("/api/auth/telegram-login")
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_telegram_login_with_invalid_signature(client: AsyncClient):
    """POST с неверной подписью → 401."""
    headers = {"X-Telegram-Init-Data": "user=%7B%22id%22%3A123%7D&hash=fakehash&auth_date=9999999999"}
    resp = await client.post("/api/auth/telegram-login", headers=headers)
    assert resp.status_code == 401


@pytest.mark.asyncio
async def test_telegram_login_with_expired_init_data(client: AsyncClient):
    """POST с просроченным initData (>24ч) → 401."""
    user_data = {"id": 123456789, "first_name": "Test", "username": "testuser"}
    init_data = make_valid_init_data(user_data, expired=True)
    
    resp = await client.post(
        "/api/auth/telegram-login",
        headers={"X-Telegram-Init-Data": init_data}
    )
    assert resp.status_code == 401
    assert "expired" in resp.json()["detail"].lower()


@pytest.mark.asyncio
async def test_telegram_login_creates_new_user(client: AsyncClient, db: AsyncSession):
    """Новый Telegram-пользователь создаётся автоматически."""
    telegram_id = 999888777
    user_data = {
        "id": telegram_id,
        "first_name": "Иван",
        "last_name": "Петров",
        "username": "ivan_p"
    }
    init_data = make_valid_init_data(user_data)
    
    # Убеждаемся что пользователя нет в БД
    result = await db.execute(select(User).where(User.telegram_id == telegram_id))
    assert result.scalar_one_or_none() is None
    
    resp = await client.post(
        "/api/auth/telegram-login",
        headers={"X-Telegram-Init-Data": init_data}
    )
    
    assert resp.status_code == 200
    body = resp.json()
    
    # Проверка токена
    assert "access_token" in body
    assert body["token_type"] == "bearer"
    
    # Проверка данных пользователя
    assert body["user"]["telegram_id"] == telegram_id
    assert body["user"]["first_name"] == "Иван"
    assert body["user"]["last_name"] == "Петров"
    assert body["user"]["username"] == "ivan_p"
    
    # Проверка что создан в БД
    await db.commit()
    result = await db.execute(select(User).where(User.telegram_id == telegram_id))
    db_user = result.scalar_one_or_none()
    assert db_user is not None
    assert db_user.email is None  # email не требуется для Telegram-пользователей
    assert db_user.hashed_password is None


@pytest.mark.asyncio
async def test_telegram_login_updates_existing_user(client: AsyncClient, db: AsyncSession):
    """При повторном входе обновляются first_name/username из Telegram."""
    telegram_id = 555666777
    
    # Создаём пользователя вручную со старыми данными
    user = User(
        telegram_id=telegram_id,
        first_name="СтароеИмя",
        username="old_username"
    )
    db.add(user)
    await db.commit()
    await db.refresh(user)
    
    # Заходим с новыми данными из Telegram
    user_data = {
        "id": telegram_id,
        "first_name": "НовоеИмя",
        "username": "new_username"
    }
    init_data = make_valid_init_data(user_data)
    
    resp = await client.post(
        "/api/auth/telegram-login",
        headers={"X-Telegram-Init-Data": init_data}
    )
    
    assert resp.status_code == 200
    assert resp.json()["user"]["first_name"] == "НовоеИмя"
    assert resp.json()["user"]["username"] == "new_username"
    
    # Проверяем в БД
    result = await db.execute(select(User).where(User.telegram_id == telegram_id))
    db_user = result.scalar_one_or_none()
    assert db_user.first_name == "НовоеИмя"
    assert db_user.username == "new_username"


@pytest.mark.asyncio
async def test_bind_telegram_to_existing_account(
    client: AsyncClient,  # ← было authenticated_client
    db_session: AsyncSession, 
    test_user: User,  # ← добавили test_user
    auth_headers: dict  # ← добавили auth_headers
):
    """Привязка Telegram к уже существующему email-аккаунту."""
    telegram_id = 111222333
    user_data = {"id": telegram_id, "first_name": "Bound", "username": "bound_user"}
    init_data = make_valid_init_data(user_data)
    
    # Используем client + auth_headers вместо authenticated_client
    resp = await client.post(
        "/api/auth/bind-telegram",
        headers={**auth_headers, "X-Telegram-Init-Data": init_data}
    )
    
    assert resp.status_code == 200
    assert resp.json()["telegram_id"] == telegram_id


@pytest.mark.asyncio
async def test_bind_telegram_already_bound_to_another_user(
    client: AsyncClient,
    db_session: AsyncSession,
    auth_headers: dict
):
    """Нельзя привязать Telegram, который уже принадлежит другому пользователю → 409."""
    telegram_id = 444555666
    
    # Создаём другого пользователя с этим telegram_id
    other_user = User(telegram_id=telegram_id, first_name="Other")
    db_session.add(other_user)
    await db_session.commit()
    
    user_data = {"id": telegram_id, "first_name": "Test"}
    init_data = make_valid_init_data(user_data)
    
    resp = await client.post(
        "/api/auth/bind-telegram",
        headers={**auth_headers, "X-Telegram-Init-Data": init_data}
    )
    
    assert resp.status_code == 409


@pytest.mark.asyncio
async def test_unbind_telegram(
    client: AsyncClient, 
    db_session: AsyncSession, 
    test_user: User,
    auth_headers: dict
):
    """Отвязка Telegram от аккаунта."""
    telegram_id = 777888999
    
    # Привязываем к test_user
    test_user.telegram_id = telegram_id
    await db_session.commit()
    
    # Отвязываем
    resp = await client.post(
        "/api/auth/unbind-telegram",
        headers=auth_headers
    )
    assert resp.status_code == 200
    
    # Проверяем
    await db_session.refresh(test_user)
    assert test_user.telegram_id is None


@pytest.mark.asyncio
async def test_unbind_telegram_when_not_bound(
    client: AsyncClient,
    test_user: User,
    auth_headers: dict
):
    """Нельзя отвязать то, что не привязано → 400."""
    # Убеждаемся что telegram_id = None
    assert test_user.telegram_id is None
    
    resp = await client.post(
        "/api/auth/unbind-telegram",
        headers=auth_headers
    )
    assert resp.status_code == 400


@pytest.mark.asyncio
async def test_get_me_returns_telegram_fields(
    client: AsyncClient,
    auth_headers: dict
):
    """GET /api/auth/me возвращает все Telegram-поля."""
    resp = await client.get("/api/auth/me", headers=auth_headers)
    assert resp.status_code == 200
    
    body = resp.json()
    assert "telegram_id" in body
    assert "first_name" in body
    assert "last_name" in body
    assert "username" in body

@pytest.mark.asyncio
async def test_bind_telegram_requires_auth(client: AsyncClient):
    """POST /api/auth/bind-telegram без токена → 401."""
    user_data = {"id": 123, "first_name": "Test"}
    init_data = make_valid_init_data(user_data)
    
    resp = await client.post(
        "/api/auth/bind-telegram",
        headers={"X-Telegram-Init-Data": init_data}
    )
    assert resp.status_code == 401