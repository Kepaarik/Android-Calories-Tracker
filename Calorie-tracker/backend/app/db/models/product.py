from datetime import datetime
from sqlalchemy import Integer, ForeignKey, Float, String, DateTime, func, Boolean
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base


class Product(Base):
    __tablename__ = "products"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=True, index=True
    )
    barcode = mapped_column(String, nullable=True, index=True)
    name: Mapped[str] = mapped_column(String(255), nullable=False, index=True)
    calories: Mapped[float] = mapped_column(Float, nullable=False)
    proteins: Mapped[float] = mapped_column(Float, nullable=False, default=0)
    fats: Mapped[float] = mapped_column(Float, nullable=False, default=0)
    carbs: Mapped[float] = mapped_column(Float, nullable=False, default=0)
    is_composite: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)  # ← НОВОЕ
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), server_default=func.now()
    )
    is_deleted: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)

    user: Mapped["User"] = relationship("User", back_populates="products")
    diary_entries: Mapped[list["DiaryEntry"]] = relationship("DiaryEntry", back_populates="product")
    
    # Ингредиенты составного блюда
    ingredients: Mapped[list["RecipeItem"]] = relationship(
        "RecipeItem",
        back_populates="dish",
        foreign_keys="RecipeItem.dish_id",
        cascade="all, delete-orphan",
    )


class RecipeItem(Base):
    """Ингредиент составного блюда"""
    __tablename__ = "recipe_items"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    dish_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("products.id", ondelete="CASCADE"), nullable=False, index=True
    )
    ingredient_id: Mapped[int] = mapped_column(
        Integer, ForeignKey("products.id", ondelete="CASCADE"), nullable=False, index=True
    )
    weight_grams: Mapped[float] = mapped_column(Float, nullable=False)

    dish: Mapped["Product"] = relationship(
        "Product", foreign_keys=[dish_id], back_populates="ingredients"
    )
    ingredient: Mapped["Product"] = relationship(
        "Product", foreign_keys=[ingredient_id]
    )