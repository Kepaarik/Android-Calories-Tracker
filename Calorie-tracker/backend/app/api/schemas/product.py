from pydantic import BaseModel, ConfigDict, Field
from typing import Optional


class ProductCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=255)
    calories: float = Field(..., ge=0)
    proteins: float = Field(0, ge=0)
    fats: float = Field(0, ge=0)
    carbs: float = Field(0, ge=0)
    barcode: str | None = None


class ProductUpdate(BaseModel):
    name: str | None = None
    calories: float | None = None
    proteins: float | None = None
    fats: float | None = None
    carbs: float | None = None
    barcode: str | None = None 
    
class IngredientItem(BaseModel):
    ingredient_id: int
    weight_grams: float = Field(..., gt=0, le=10000)


class CompositeProductCreate(BaseModel):
    name: str = Field(..., min_length=1, max_length=255)
    total_weight: float = Field(..., gt=0)  # общий вес блюда в граммах
    ingredients: list[IngredientItem] = Field(..., min_length=1)


class ProductResponse(BaseModel):
    id: int
    name: str
    barcode: str | None
    calories: float
    proteins: float
    fats: float
    carbs: float
    is_composite: bool = False

    model_config = ConfigDict(from_attributes=True)


class ProductWithIngredients(ProductResponse):
    ingredients: list["IngredientResponse"] = []


class IngredientResponse(BaseModel):
    id: int
    ingredient_id: int
    weight_grams: float
    ingredient: Optional[ProductResponse] = None

    model_config = ConfigDict(from_attributes=True)