# backend/app/api/routers/weight.py
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.ext.asyncio import AsyncSession
from datetime import datetime
from app.core.database import get_db
from app.api.deps import get_current_user
from app.db.models.user import User
from app.db.crud import weight as weight_crud
from app.api.schemas.weight import WeightEntryCreate, WeightEntryResponse, WeightStats

router = APIRouter(prefix="/api/weight", tags=["weight"])

@router.get("/", response_model=list[WeightEntryResponse])
async def get_weight_entries(
    limit: int = Query(30, ge=1, le=100),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Получить записи о весе"""
    entries = await weight_crud.get_user_weight_entries(db, current_user.id, limit)
    return entries

@router.post("/", response_model=WeightEntryResponse)
async def add_weight_entry(
    entry_data: WeightEntryCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Добавить запись о весе"""
    recorded_at = None
    if entry_data.date:
        try:
            target_date = datetime.strptime(entry_data.date, "%Y-%m-%d").date()
            recorded_at = datetime.combine(target_date, datetime.min.time())
        except ValueError:
            raise HTTPException(status_code=400, detail="Неверный формат даты")
    
    entry = await weight_crud.create_weight_entry(
        db,
        current_user.id,
        entry_data.weight_kg,
        recorded_at
    )
    return entry

@router.delete("/{entry_id}")
async def delete_weight_entry(
    entry_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Удалить запись о весе"""
    success = await weight_crud.delete_weight_entry(db, current_user.id, entry_id)
    if not success:
        raise HTTPException(status_code=404, detail="Запись не найдена")
    return {"message": "Запись удалена"}

@router.get("/stats", response_model=WeightStats)
async def get_weight_stats(
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Получить статистику по весу"""
    stats = await weight_crud.get_weight_stats(db, current_user.id)
    return WeightStats(**stats)