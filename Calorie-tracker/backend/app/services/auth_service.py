from jose import JWTError, jwt
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.schemas.auth import UserCreate
from app.core.config import settings
from app.core.security import get_password_hash, verify_password
from app.db.crud.users import create_user, get_user_by_email


async def authenticate_user(db: AsyncSession, email: str, password: str):
    user = await get_user_by_email(db, email)
    if not user:
        return None
    if not verify_password(password, user.hashed_password):
        return None
    return user


async def register_user(db: AsyncSession, user_data: UserCreate):
    existing_user = await get_user_by_email(db, user_data.email)
    if existing_user:
        raise ValueError("User with this email already exists")
    user = await create_user(db, user_data.email, user_data.password)
    return user


async def get_current_user_from_token(db: AsyncSession, token: str):
    try:
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        user_id: str = payload.get("sub")
        if user_id is None:
            return None
        from app.db.crud.users import get_user_by_id
        user = await get_user_by_id(db, int(user_id))
        return user
    except JWTError:
        return None
    