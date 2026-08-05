# backend/app/db/models/diary.py
# backend/app/db/models/diary.py
from datetime import datetime
from sqlalchemy import Integer, ForeignKey, Float, String, DateTime, func, Boolean
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base

class DiaryEntry(Base):
    __tablename__ = "diary_entries"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(
        Integer, 
        ForeignKey("users.id", ondelete="CASCADE"), 
        nullable=False,
        index=True
    )
    product_id: Mapped[int] = mapped_column(
        Integer, 
        ForeignKey("products.id", ondelete="CASCADE"), 
        nullable=False
    )
    weight_grams: Mapped[float] = mapped_column(Float, nullable=False)
    meal_type: Mapped[str] = mapped_column(String(32), nullable=False)
    consumed_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), 
        server_default=func.now(),
        index=True
    )
    is_deleted: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    
    user: Mapped["User"] = relationship("User", back_populates="diary_entries")
    product: Mapped["Product"] = relationship("Product", back_populates="diary_entries")

    def __repr__(self) -> str:
        return f"<DiaryEntry(id={self.id}, user_id={self.user_id}, product_id={self.product_id})>"