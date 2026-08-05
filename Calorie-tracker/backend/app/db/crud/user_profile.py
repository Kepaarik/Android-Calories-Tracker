# backend/app/db/crud/user_profile.py
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from app.db.models.user_profile import (
    UserProfile,
    Gender,
    ActivityLevel,
    FitnessGoal,
    CalculationFormula,
)
from app.services.calorie_calculator import calculate_daily_needs

async def get_user_profile(db: AsyncSession, user_id: int) -> UserProfile | None:
    """Получить профиль пользователя"""
    result = await db.execute(
        select(UserProfile).where(UserProfile.user_id == user_id)
    )
    return result.scalar_one_or_none()

async def get_or_create_profile(db: AsyncSession, user_id: int) -> UserProfile:
    """Получить профиль пользователя, создав его с значениями по умолчанию,
    если пользователь ещё не заполнял анкету (нужно для UI-настроек вроде
    темы и порядка виджетов, не зависящих от данных о питании)"""
    profile = await get_user_profile(db, user_id)
    if profile:
        return profile

    profile = UserProfile(
        user_id=user_id,
        gender=Gender.MALE,
        age=25,
        weight_kg=70,
        height_cm=175,
        activity_level=ActivityLevel.MODERATE,
        fitness_goal=FitnessGoal.MAINTAIN,
        calculation_formula=CalculationFormula.MIFFLIN_ST_JEOR,
        calorie_adjustment=0,
    )
    db.add(profile)
    await db.commit()
    await db.refresh(profile)
    return profile

async def create_or_update_profile(
    db: AsyncSession,
    user_id: int,
    **kwargs
) -> UserProfile:
    """Создать или обновить профиль"""
    result = await db.execute(
        select(UserProfile).where(UserProfile.user_id == user_id)
    )
    profile = result.scalar_one_or_none()
    
    if profile:
        for key, value in kwargs.items():
            if value is not None:
                setattr(profile, key, value)
    else:
        profile = UserProfile(user_id=user_id, **kwargs)
        db.add(profile)
    
    await db.commit()
    await db.refresh(profile)
    return profile

async def get_profile_with_calculated_goals(db: AsyncSession, user_id: int) -> dict | None:
    profile = await get_user_profile(db, user_id)
    if not profile:
        return None
    
    # Возвращаем профиль как есть - расчёт делается в роутере
    return {
        "profile": profile,
    }

async def update_dashboard_settings(
    db: AsyncSession,
    user_id: int,
    settings: dict
) -> UserProfile:
    """Обновить порядок виджетов статистики"""
    profile = await get_or_create_profile(db, user_id)

    profile.dashboard_settings = settings
    await db.commit()
    await db.refresh(profile)
    return profile

async def get_dashboard_settings(
    db: AsyncSession,
    user_id: int
) -> dict | None:
    """Получить порядок виджетов статистики"""
    profile = await get_user_profile(db, user_id)
    if not profile:
        return None

    return profile.dashboard_settings

async def update_theme_settings(
    db: AsyncSession,
    user_id: int,
    theme: str | None,
    accent_color: str | None,
) -> UserProfile:
    """Обновить тему и основной цвет"""
    profile = await get_or_create_profile(db, user_id)

    profile.theme = theme
    profile.accent_color = accent_color
    await db.commit()
    await db.refresh(profile)
    return profile

async def get_theme_settings(
    db: AsyncSession,
    user_id: int
) -> UserProfile | None:
    """Получить тему и основной цвет"""
    return await get_user_profile(db, user_id)