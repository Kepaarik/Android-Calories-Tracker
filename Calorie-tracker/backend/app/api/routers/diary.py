from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from sqlalchemy.orm import selectinload
from datetime import datetime

from app.core.database import get_db
from app.db.models.diary import DiaryEntry
from app.db.models.user import User
from app.api.schemas.diary import DiaryEntryCreate, DiaryEntryUpdate, DiaryEntryResponse, DailySummary
from app.api.deps import get_current_user

router = APIRouter(prefix="/api/diary", tags=["diary"])


@router.get("/", response_model=list[DiaryEntryResponse])
async def get_entries(
    date: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    start = datetime.fromisoformat(date)
    end = start.replace(hour=23, minute=59, second=59)

    result = await db.execute(
        select(DiaryEntry)
        .options(selectinload(DiaryEntry.product))
        .where(
            DiaryEntry.user_id == current_user.id,
            DiaryEntry.consumed_at >= start,
            DiaryEntry.consumed_at <= end,
            DiaryEntry.is_deleted == False,
        )
        .order_by(DiaryEntry.consumed_at.asc())
    )
    return result.scalars().all()


@router.post("/", response_model=DiaryEntryResponse, status_code=201)
async def add_entry(
    entry_data: DiaryEntryCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    if entry_data.consumed_at:
        consumed_at = datetime.fromisoformat(entry_data.consumed_at)
    else:
        [year, month, day] = entry_data.date.split("-")
        year, month, day = int(year), int(month), int(day)
        now = datetime.now()
        consumed_at = now.replace(year=year, month=month, day=day)

    entry = DiaryEntry(
        user_id=current_user.id,
        product_id=entry_data.product_id,
        weight_grams=entry_data.weight_grams,
        meal_type=entry_data.meal_type,
        consumed_at=consumed_at,
    )
    db.add(entry)
    await db.commit()
    await db.refresh(entry, attribute_names=["product"])
    return entry


@router.put("/{entry_id}", response_model=DiaryEntryResponse)
async def update_diary_entry(
    entry_id: int,
    entry_data: DiaryEntryUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    result = await db.execute(
        select(DiaryEntry)
        .options(selectinload(DiaryEntry.product))
        .where(DiaryEntry.id == entry_id, DiaryEntry.user_id == current_user.id)
    )
    entry = result.scalar_one_or_none()

    if not entry:
        raise HTTPException(status_code=404, detail="Запись не найдена")

    if entry_data.weight_grams is not None:
        entry.weight_grams = entry_data.weight_grams
    if entry_data.meal_type is not None:
        entry.meal_type = entry_data.meal_type
    if entry_data.consumed_at is not None:
        entry.consumed_at = datetime.fromisoformat(entry_data.consumed_at)

    await db.commit()
    await db.refresh(entry, attribute_names=["product"])
    return entry


@router.delete("/{entry_id}", status_code=204)
async def delete_entry(
    entry_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    result = await db.execute(
        select(DiaryEntry).where(
            DiaryEntry.id == entry_id,
            DiaryEntry.user_id == current_user.id,
        )
    )
    entry = result.scalar_one_or_none()

    if not entry:
        raise HTTPException(status_code=404, detail="Запись не найдена")

    entry.is_deleted = True
    await db.commit()


@router.get("/summary", response_model=DailySummary)
async def get_summary(
    date: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    start = datetime.fromisoformat(date)
    end = start.replace(hour=23, minute=59, second=59)

    result = await db.execute(
        select(DiaryEntry)
        .options(selectinload(DiaryEntry.product))
        .where(
            DiaryEntry.user_id == current_user.id,
            DiaryEntry.consumed_at >= start,
            DiaryEntry.consumed_at <= end,
            DiaryEntry.is_deleted == False,
        )
    )
    entries = result.scalars().all()

    total_calories = sum(
        (float(e.product.calories) * float(e.weight_grams) / 100)
        for e in entries if e.product
    )
    total_proteins = sum(
        (float(e.product.proteins) * float(e.weight_grams) / 100)
        for e in entries if e.product
    )
    total_fats = sum(
        (float(e.product.fats) * float(e.weight_grams) / 100)
        for e in entries if e.product
    )
    total_carbs = sum(
        (float(e.product.carbs) * float(e.weight_grams) / 100)
        for e in entries if e.product
    )

    return DailySummary(
        calories=round(total_calories, 1),
        proteins=round(total_proteins, 1),
        fats=round(total_fats, 1),
        carbs=round(total_carbs, 1),
        entries_count=len(entries),
    )