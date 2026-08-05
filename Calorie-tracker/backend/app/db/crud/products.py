# backend/app/db/crud/products.py
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.db.models.product import Product

async def get_product_by_id(db: AsyncSession, product_id: int) -> Product | None:
    result = await db.execute(select(Product).where(Product.id == product_id))
    return result.scalar_one_or_none()

async def get_products(db: AsyncSession, skip: int = 0, limit: int = 100) -> list[Product]:
    result = await db.execute(select(Product).offset(skip).limit(limit))
    return result.scalars().all()

async def search_products(db: AsyncSession, query: str, limit: int = 20) -> list[Product]:
    """Поиск продуктов (регистронезависимый, работает с кириллицей)"""
    query = query.strip()
    
    # ilike() сгенерирует: lower(name) LIKE lower(?)
    # А благодаря нашей кастомной функции lower() в SQLite, кириллица будет работать!
    result = await db.execute(
        select(Product)
        .where(Product.name.ilike(f"%{query}%"))
        .limit(limit)
    )
    return result.scalars().all()

async def create_product(
    db: AsyncSession,
    name: str,
    calories: float,
    proteins: float,
    fats: float,
    carbs: float
) -> Product:
    product = Product(
        name=name,
        calories=calories,
        proteins=proteins,
        fats=fats,
        carbs=carbs
    )
    db.add(product)
    await db.commit()
    await db.refresh(product)
    return product