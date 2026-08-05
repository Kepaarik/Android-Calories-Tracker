# backend/app/db/crud/weight.py
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from datetime import datetime, date
from app.db.models.weight_entry import WeightEntry

async def get_user_weight_entries(
    db: AsyncSession,
    user_id: int,
    limit: int = 30
) -> list[WeightEntry]:
    """Получить последние записи о весе"""
    result = await db.execute(
        select(WeightEntry)
        .where(WeightEntry.user_id == user_id)
        .order_by(WeightEntry.recorded_at.desc())
        .limit(limit)
    )
    return list(result.scalars().all())

async def create_weight_entry(
    db: AsyncSession,
    user_id: int,
    weight_kg: float,
    recorded_at: datetime | None = None
) -> WeightEntry:
    """Создать запись о весе"""
    if recorded_at is None:
        recorded_at = datetime.now()
    
    entry = WeightEntry(
        user_id=user_id,
        weight_kg=weight_kg,
        recorded_at=recorded_at
    )
    db.add(entry)
    await db.commit()
    await db.refresh(entry)
    return entry

async def delete_weight_entry(
    db: AsyncSession,
    user_id: int,
    entry_id: int
) -> bool:
    """Удалить запись о весе"""
    result = await db.execute(
        select(WeightEntry).where(
            WeightEntry.id == entry_id,
            WeightEntry.user_id == user_id
        )
    )
    entry = result.scalar_one_or_none()
    
    if not entry:
        return False
    
    await db.delete(entry)
    await db.commit()
    return True

async def get_weight_stats(
    db: AsyncSession,
    user_id: int
) -> dict:
    """Получить статистику по весу"""
    # Текущий вес (последняя запись)
    current_result = await db.execute(
        select(WeightEntry.weight_kg)
        .where(WeightEntry.user_id == user_id)
        .order_by(WeightEntry.recorded_at.desc())
        .limit(1)
    )
    current_weight = current_result.scalar_one_or_none()
    
    # Предыдущий вес
    previous_result = await db.execute(
        select(WeightEntry.weight_kg)
        .where(WeightEntry.user_id == user_id)
        .order_by(WeightEntry.recorded_at.desc())
        .offset(1)
        .limit(1)
    )
    previous_weight = previous_result.scalar_one_or_none()
    
    # Мин/макс
    stats_result = await db.execute(
        select(
            func.min(WeightEntry.weight_kg).label('min_weight'),
            func.max(WeightEntry.weight_kg).label('max_weight'),
            func.count(WeightEntry.id).label('entries_count')
        )
        .where(WeightEntry.user_id == user_id)
    )
    stats = stats_result.first()
    
    return {
        "current_weight": current_weight,
        "previous_weight": previous_weight,
        "change": (current_weight - previous_weight) if current_weight and previous_weight else None,
        "min_weight": stats.min_weight if stats else None,
        "max_weight": stats.max_weight if stats else None,
        "entries_count": stats.entries_count if stats else 0,
    }