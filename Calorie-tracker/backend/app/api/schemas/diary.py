from pydantic import BaseModel, ConfigDict, Field
from datetime import datetime
from typing import Optional


class ProductBrief(BaseModel):
    id: int
    name: str
    calories: float
    proteins: float
    fats: float
    carbs: float

    model_config = ConfigDict(from_attributes=True)


class DiaryEntryCreate(BaseModel):
    product_id: int
    weight_grams: float = Field(gt=0, le=10000)  # ← Добавлена валидация
    meal_type: str
    date: str
    consumed_at: Optional[str] = None


class DiaryEntryUpdate(BaseModel):
    weight_grams: Optional[float] = Field(None, gt=0, le=10000)  # ← Добавлена валидация
    meal_type: Optional[str] = None
    consumed_at: Optional[str] = None


class DiaryEntryResponse(BaseModel):
    id: int
    product_id: int
    weight_grams: float
    meal_type: str
    consumed_at: datetime
    product: Optional[ProductBrief] = None

    model_config = ConfigDict(from_attributes=True)


class DailySummary(BaseModel):
    calories: float = 0
    proteins: float = 0
    fats: float = 0
    carbs: float = 0
    entries_count: int = 0