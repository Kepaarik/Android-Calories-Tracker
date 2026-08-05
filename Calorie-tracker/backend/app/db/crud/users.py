# backend/app/db/crud/users.py
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.db.models.user import User
from app.core.security import get_password_hash


async def get_user_by_email(db: AsyncSession, email: str) -> User | None:
    result = await db.execute(select(User).where(User.email == email))
    return result.scalar_one_or_none()


async def get_user_by_id(db: AsyncSession, user_id: int) -> User | None:
    result = await db.execute(select(User).where(User.id == user_id))
    return result.scalar_one_or_none()


async def get_user_by_telegram_id(db: AsyncSession, telegram_id: int) -> User | None:
    result = await db.execute(select(User).where(User.telegram_id == telegram_id))
    return result.scalar_one_or_none()


async def create_user(db: AsyncSession, email: str, password: str) -> User:
    hashed_password = get_password_hash(password)
    user = User(email=email, hashed_password=hashed_password)
    db.add(user)
    await db.commit()
    await db.refresh(user)
    return user


async def create_telegram_user(
    db: AsyncSession,
    telegram_id: int,
    first_name: str,
    last_name: str,
    username: str | None,
) -> User:
    user = User(
        telegram_id=telegram_id,
        email=f"{telegram_id}@telegram.local",
        hashed_password=None,
        first_name=first_name,
        last_name=last_name,
        username=username,
    )
    db.add(user)
    await db.commit()
    await db.refresh(user)
    return user