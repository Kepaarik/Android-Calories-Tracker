# backend/app/api/routers/user_profile.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.api.deps import get_current_user
from app.db.models.user import User
from app.api.schemas.user_profile import (
    UserProfileCreate,
    UserProfileUpdate,
    UserProfileResponse,
    DashboardSettingsSchema,
    ThemeSettingsSchema,
)
from app.db.crud import user_profile as profile_crud
from app.services.calorie_calculator import calculate_daily_needs

router = APIRouter(prefix="/api/profile", tags=["profile"])


def build_response(profile, calculated: dict) -> UserProfileResponse:
    """Вспомогательная функция для сборки ответа"""
    goals = {
        "calories": profile.custom_calorie_goal or calculated["calories"],
        "proteins": profile.custom_protein_goal or calculated["proteins"],
        "fats": profile.custom_fat_goal or calculated["fats"],
        "carbs": profile.custom_carb_goal or calculated["carbs"],
    }
    
    return UserProfileResponse(
        id=profile.id,
        user_id=profile.user_id,
        gender=profile.gender,
        age=profile.age,
        weight_kg=profile.weight_kg,
        height_cm=profile.height_cm,
        activity_level=profile.activity_level,
        fitness_goal=profile.fitness_goal,
        calculation_formula=profile.calculation_formula,
        calorie_adjustment=profile.calorie_adjustment,
        custom_calorie_goal=profile.custom_calorie_goal,
        custom_protein_goal=profile.custom_protein_goal,
        custom_fat_goal=profile.custom_fat_goal,
        custom_carb_goal=profile.custom_carb_goal,
        dashboard_settings=profile.dashboard_settings,
        theme=profile.theme,
        accent_color=profile.accent_color,
        calculated_calories=goals["calories"],
        calculated_proteins=goals["proteins"],
        calculated_fats=goals["fats"],
        calculated_carbs=goals["carbs"],
        created_at=profile.created_at,
        updated_at=profile.updated_at,
    )


@router.get("/", response_model=UserProfileResponse)
async def get_profile(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Получить профиль пользователя"""
    profile_data = await profile_crud.get_profile_with_calculated_goals(db, current_user.id)
    
    if not profile_data:
        raise HTTPException(status_code=404, detail="Профиль не найден")
    
    profile = profile_data["profile"]
    calculated = calculate_daily_needs(
        gender=profile.gender,
        age=profile.age,
        weight_kg=profile.weight_kg,
        height_cm=profile.height_cm,
        activity_level=profile.activity_level,
        fitness_goal=profile.fitness_goal,
        formula=profile.calculation_formula,
        calorie_adjustment=profile.calorie_adjustment,
    )
    
    return build_response(profile, calculated)


@router.post("/", response_model=UserProfileResponse)
async def create_profile(
    profile_data: UserProfileCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Создать профиль пользователя"""
    profile = await profile_crud.create_or_update_profile(
        db,
        current_user.id,
        gender=profile_data.gender,
        age=profile_data.age,
        weight_kg=profile_data.weight_kg,
        height_cm=profile_data.height_cm,
        activity_level=profile_data.activity_level,
        fitness_goal=profile_data.fitness_goal,
        calculation_formula=profile_data.calculation_formula,
        calorie_adjustment=profile_data.calorie_adjustment,
        custom_calorie_goal=profile_data.custom_calorie_goal,
        custom_protein_goal=profile_data.custom_protein_goal,
        custom_fat_goal=profile_data.custom_fat_goal,
        custom_carb_goal=profile_data.custom_carb_goal,
        dashboard_settings=profile_data.dashboard_settings.model_dump() if profile_data.dashboard_settings else None,
    )
    
    calculated = calculate_daily_needs(
        gender=profile.gender,
        age=profile.age,
        weight_kg=profile.weight_kg,
        height_cm=profile.height_cm,
        activity_level=profile.activity_level,
        fitness_goal=profile.fitness_goal,
        formula=profile.calculation_formula,
        calorie_adjustment=profile.calorie_adjustment,
    )
    
    return build_response(profile, calculated)


@router.put("/", response_model=UserProfileResponse)
async def update_profile(
    profile_data: UserProfileUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Обновить профиль пользователя"""
    update_data = profile_data.model_dump(exclude_unset=True)
    
    if not update_data:
        raise HTTPException(status_code=400, detail="Нет данных для обновления")
    
    # Конвертируем dashboard_settings в dict если есть
    if 'dashboard_settings' in update_data and update_data['dashboard_settings']:
        update_data['dashboard_settings'] = update_data['dashboard_settings'].model_dump()
    
    profile = await profile_crud.create_or_update_profile(
        db,
        current_user.id,
        **update_data,
    )
    
    calculated = calculate_daily_needs(
        gender=profile.gender,
        age=profile.age,
        weight_kg=profile.weight_kg,
        height_cm=profile.height_cm,
        activity_level=profile.activity_level,
        fitness_goal=profile.fitness_goal,
        formula=profile.calculation_formula,
        calorie_adjustment=profile.calorie_adjustment,
    )
    
    return build_response(profile, calculated)


@router.get("/dashboard-settings")
async def get_dashboard_settings(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Получить порядок виджетов статистики"""
    settings = await profile_crud.get_dashboard_settings(db, current_user.id)

    if not settings:
        return {
            "widgets": [
                {"id": "summary", "visible": True, "order": 0},
                {"id": "water", "visible": True, "order": 1},
                {"id": "weekly_stats", "visible": True, "order": 2},
                {"id": "weight", "visible": True, "order": 3},
            ],
        }

    return settings


@router.put("/dashboard-settings")
async def save_dashboard_settings(
    settings: DashboardSettingsSchema,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Сохранить порядок виджетов статистики"""
    profile = await profile_crud.update_dashboard_settings(
        db,
        current_user.id,
        settings.model_dump()
    )

    return {"message": "Настройки сохранены", "settings": profile.dashboard_settings}


@router.get("/theme-settings", response_model=ThemeSettingsSchema)
async def get_theme_settings(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Получить тему и основной цвет"""
    profile = await profile_crud.get_theme_settings(db, current_user.id)

    if not profile:
        return ThemeSettingsSchema()

    return ThemeSettingsSchema(theme=profile.theme, accent_color=profile.accent_color)


@router.put("/theme-settings", response_model=ThemeSettingsSchema)
async def save_theme_settings(
    settings: ThemeSettingsSchema,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Сохранить тему и основной цвет"""
    profile = await profile_crud.update_theme_settings(
        db,
        current_user.id,
        theme=settings.theme,
        accent_color=settings.accent_color,
    )

    return ThemeSettingsSchema(theme=profile.theme, accent_color=profile.accent_color)


@router.post("/calculate-preview")
async def calculate_preview(data: UserProfileCreate):
    """Предварительный расчёт без сохранения (используется фронтом)."""
    calculated = calculate_daily_needs(
        gender=data.gender,
        age=data.age,
        weight_kg=data.weight_kg,
        height_cm=data.height_cm,
        activity_level=data.activity_level,
        fitness_goal=data.fitness_goal,
        formula=data.calculation_formula,
        calorie_adjustment=data.calorie_adjustment,
    )
    return {
        "calories": calculated["calories"],
        "proteins": calculated["proteins"],
        "fats": calculated["fats"],
        "carbs": calculated["carbs"],
    }