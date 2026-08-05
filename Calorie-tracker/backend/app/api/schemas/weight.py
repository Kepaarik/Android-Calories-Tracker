# backend/app/api/schemas/weight.py
from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime


class WeightEntryCreate(BaseModel):
    weight_kg: float = Field(ge=30, le=300)
    date: str | None = None


class WeightEntryResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)  # ← было class Config

    id: int
    weight_kg: float
    recorded_at: datetime


class WeightStats(BaseModel):
    current_weight: float | None = None
    previous_weight: float | None = None
    change: float | None = None
    min_weight: float | None = None
    max_weight: float | None = None
    entries_count: int = 0