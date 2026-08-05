from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from sqlalchemy.orm import selectinload
from typing import Optional

from app.core.database import get_db
from app.db.models.product import Product, RecipeItem
from app.db.models.user import User
from app.api.schemas.product import (
    ProductCreate,
    ProductUpdate,
    ProductResponse,
    CompositeProductCreate,
    ProductWithIngredients,
)
from app.api.deps import get_current_user

router = APIRouter(prefix="/api/products", tags=["products"])


@router.post("/", response_model=ProductResponse, status_code=201)
async def create_product(
    product_data: ProductCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Создать обычный продукт"""
    product = Product(
        user_id=None,
        name=product_data.name,
        calories=product_data.calories,
        proteins=product_data.proteins,
        fats=product_data.fats,
        carbs=product_data.carbs,
        is_composite=False,
    )
    db.add(product)
    await db.commit()
    await db.refresh(product)
    return product


@router.post("/composite", response_model=ProductWithIngredients, status_code=201)
async def create_composite_product(
    data: CompositeProductCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Создать составное блюдо из существующих продуктов"""

    # Загружаем ингредиенты
    ingredient_ids = [i.ingredient_id for i in data.ingredients]
    result = await db.execute(
        select(Product).where(
            Product.id.in_(ingredient_ids),
            Product.is_deleted == False,
        )
    )
    ingredients = {p.id: p for p in result.scalars().all()}
    
    if len(ingredients) != len(ingredient_ids):
        raise HTTPException(status_code=404, detail="Один или несколько продуктов не найдены")
    
    # Считаем общие КБЖУ
    total_calories = 0.0
    total_proteins = 0.0
    total_fats = 0.0
    total_carbs = 0.0
    
    for item in data.ingredients:
        ing = ingredients[item.ingredient_id]
        factor = item.weight_grams / 100.0
        total_calories += float(ing.calories) * factor
        total_proteins += float(ing.proteins) * factor
        total_fats += float(ing.fats) * factor
        total_carbs += float(ing.carbs) * factor
    
    # Пересчитываем на 100г готового блюда
    factor_per_100g = 100.0 / data.total_weight
    dish = Product(
        user_id=None,
        name=data.name,
        calories=round(total_calories * factor_per_100g, 1),
        proteins=round(total_proteins * factor_per_100g, 1),
        fats=round(total_fats * factor_per_100g, 1),
        carbs=round(total_carbs * factor_per_100g, 1),
        is_composite=True,
    )
    db.add(dish)
    await db.flush()
    
    # Сохраняем связи
    for item in data.ingredients:
        recipe_item = RecipeItem(
            dish_id=dish.id,
            ingredient_id=item.ingredient_id,
            weight_grams=item.weight_grams,
        )
        db.add(recipe_item)
    
    await db.commit()
    
    # Возвращаем с загруженными ингредиентами
    result = await db.execute(
        select(Product)
        .options(selectinload(Product.ingredients).selectinload(RecipeItem.ingredient))
        .where(Product.id == dish.id)
    )
    return result.scalar_one()


@router.get("/", response_model=list[ProductResponse])
async def get_products(
    search: Optional[str] = Query(None),  # ← Убрали min_length=2
    sort_by: Optional[str] = Query("name"), # ← Добавили сортировку
    sort_order: Optional[str] = Query("asc"),
    include_composite: bool = Query(True),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Получить список продуктов"""
    query = select(Product).where(
        Product.is_deleted == False,
    )
    
    if not include_composite:
        query = query.where(Product.is_composite == False)
    
    # Фильтр по поиску (игнорируем пустые строки и пробелы)
    if search and search.strip():
        query = query.where(Product.name.ilike(f"%{search.strip()}%"))
    
    # Сортировка
    sort_column = getattr(Product, sort_by, Product.name)
    if sort_order == "desc":
        query = query.order_by(sort_column.desc())
    else:
        query = query.order_by(sort_column.asc())
        
    result = await db.execute(query)
    return result.scalars().all()


@router.get("/{product_id}", response_model=ProductWithIngredients)
async def get_product(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Получить один продукт с ингредиентами"""
    result = await db.execute(
        select(Product)
        .options(selectinload(Product.ingredients).selectinload(RecipeItem.ingredient))
        .where(
            Product.id == product_id,
            Product.is_deleted == False,
        )
    )
    product = result.scalar_one_or_none()
    if not product:
        raise HTTPException(status_code=404, detail="Продукт не найден")
    return product


@router.put("/{product_id}", response_model=ProductResponse)
async def update_product(
    product_id: int,
    product_data: ProductUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Обновить продукт"""
    result = await db.execute(
        select(Product).where(
            Product.id == product_id,
            Product.is_deleted == False,
        )
    )
    product = result.scalar_one_or_none()
    if not product:
        raise HTTPException(status_code=404, detail="Продукт не найден")

    update_data = product_data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(product, field, value)

    await db.commit()
    await db.refresh(product)
    return product


@router.delete("/{product_id}", status_code=204)
async def delete_product(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    """Удалить продукт"""
    result = await db.execute(
        select(Product).where(
            Product.id == product_id,
            Product.is_deleted == False,
        )
    )
    product = result.scalar_one_or_none()
    if not product:
        raise HTTPException(status_code=404, detail="Продукт не найден")

    product.is_deleted = True
    await db.commit()