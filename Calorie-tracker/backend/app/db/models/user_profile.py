# backend/app/db/models/user_profile.py
from datetime import datetime
from sqlalchemy import Integer, ForeignKey, Float, String, DateTime, Enum as SQLEnum, func, JSON
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.core.database import Base
import enum

class Gender(str, enum.Enum):
    MALE = "male"
    FEMALE = "female"

class ActivityLevel(str, enum.Enum):
    SEDENTARY = "sedentary"
    LIGHT = "light"
    MODERATE = "moderate"
    ACTIVE = "active"
    VERY_ACTIVE = "very_active"

class FitnessGoal(str, enum.Enum):
    LOSE = "lose"
    MAINTAIN = "maintain"
    GAIN = "gain"

# ← ДОЛЖНО БЫТЬ ЗДЕСЬ
class CalculationFormula(str, enum.Enum):
    MIFFLIN_ST_JEOR = "mifflin_st_jeor"
    HARRIS_BENEDICT = "harris_benedict"
    KATCH_MCARDLE = "katch_mcardle"

class UserProfile(Base):
    __tablename__ = "user_profiles"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        unique=True
    )
    gender: Mapped[Gender] = mapped_column(SQLEnum(Gender), nullable=False)
    age: Mapped[int] = mapped_column(Integer, nullable=False)
    weight_kg: Mapped[float] = mapped_column(Float, nullable=False)
    height_cm: Mapped[float] = mapped_column(Float, nullable=False)
    activity_level: Mapped[ActivityLevel] = mapped_column(
        SQLEnum(ActivityLevel),
        default=ActivityLevel.MODERATE,
        nullable=False
    )
    fitness_goal: Mapped[FitnessGoal] = mapped_column(
        SQLEnum(FitnessGoal),
        default=FitnessGoal.MAINTAIN,
        nullable=False
    )
    # ← ДОЛЖНЫ БЫТЬ ЗДЕСЬ
    calculation_formula: Mapped[CalculationFormula] = mapped_column(
    SQLEnum(CalculationFormula),
    default=CalculationFormula.MIFFLIN_ST_JEOR,
    server_default='MIFFLIN_ST_JEOR',  # ← ДОБАВЛЕНО
    nullable=False
    )
    calorie_adjustment: Mapped[int] = mapped_column(
        Integer,
        default=0,
        server_default='0',  # ← ДОБАВЛЕНО
        nullable=False
    )
    
    custom_calorie_goal: Mapped[float | None] = mapped_column(Float, nullable=True)
    custom_protein_goal: Mapped[float | None] = mapped_column(Float, nullable=True)
    custom_fat_goal: Mapped[float | None] = mapped_column(Float, nullable=True)
    custom_carb_goal: Mapped[float | None] = mapped_column(Float, nullable=True)
    dashboard_settings: Mapped[dict | None] = mapped_column(JSON, nullable=True)
    theme: Mapped[str | None] = mapped_column(String, nullable=True)
    accent_color: Mapped[str | None] = mapped_column(String, nullable=True)
    
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        server_default=func.now()
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        server_default=func.now(),
        onupdate=func.now()
    )

    user: Mapped["User"] = relationship("User", back_populates="profile")