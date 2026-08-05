# backend/app/api/schemas/user_profile.py
from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime
from app.db.models.user_profile import Gender, ActivityLevel, FitnessGoal, CalculationFormula
from typing import Optional


class WidgetConfig(BaseModel):
    id: str
    visible: bool = True
    order: int = 0


class DashboardSettingsSchema(BaseModel):
    model_config = ConfigDict(from_attributes=True)  # ← было class Config

    widgets: list[WidgetConfig] = []


class ThemeSettingsSchema(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    theme: str | None = None
    accent_color: str | None = None


class UserProfileCreate(BaseModel):
    gender: Gender
    age: int = Field(ge=10, le=120)
    weight_kg: float = Field(ge=30, le=300)
    height_cm: float = Field(ge=100, le=250)
    activity_level: ActivityLevel = ActivityLevel.MODERATE
    fitness_goal: FitnessGoal = FitnessGoal.MAINTAIN
    calculation_formula: CalculationFormula = CalculationFormula.MIFFLIN_ST_JEOR
    calorie_adjustment: int = Field(default=0, ge=-1000, le=1000)
    custom_calorie_goal: float | None = None
    custom_protein_goal: float | None = None
    custom_fat_goal: float | None = None
    custom_carb_goal: float | None = None
    dashboard_settings: DashboardSettingsSchema | None = None


class UserProfileUpdate(BaseModel):
    gender: Gender | None = None
    age: int | None = Field(default=None, ge=10, le=120)
    weight_kg: float | None = Field(default=None, ge=30, le=300)
    height_cm: float | None = Field(default=None, ge=100, le=250)
    activity_level: ActivityLevel | None = None
    fitness_goal: FitnessGoal | None = None
    calculation_formula: CalculationFormula | None = None
    calorie_adjustment: int | None = Field(default=None, ge=-1000, le=1000)
    custom_calorie_goal: float | None = None
    custom_protein_goal: float | None = None
    custom_fat_goal: float | None = None
    custom_carb_goal: float | None = None
    dashboard_settings: DashboardSettingsSchema | None = None


class UserProfileResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)  # ← было class Config

    id: int
    user_id: int
    gender: Gender
    age: int
    weight_kg: float
    height_cm: float
    activity_level: ActivityLevel
    fitness_goal: FitnessGoal
    calculation_formula: CalculationFormula
    calorie_adjustment: int
    custom_calorie_goal: float | None = None
    custom_protein_goal: float | None = None
    custom_fat_goal: float | None = None
    custom_carb_goal: float | None = None
    dashboard_settings: DashboardSettingsSchema | None = None
    theme: str | None = None
    accent_color: str | None = None
    calculated_calories: float
    calculated_proteins: float
    calculated_fats: float
    calculated_carbs: float
    created_at: datetime
    updated_at: datetime