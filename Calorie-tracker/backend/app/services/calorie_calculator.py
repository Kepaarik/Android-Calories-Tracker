# backend/app/services/calorie_calculator.py
from app.db.models.user_profile import Gender, ActivityLevel, FitnessGoal, CalculationFormula

# ← Коэффициенты активности
ACTIVITY_MULTIPLIERS = {
    ActivityLevel.SEDENTARY: 1.2,
    ActivityLevel.LIGHT: 1.375,
    ActivityLevel.MODERATE: 1.55,
    ActivityLevel.ACTIVE: 1.725,
    ActivityLevel.VERY_ACTIVE: 1.9,
}

# ← Коэффициенты целей
GOAL_MULTIPLIERS = {
    FitnessGoal.LOSE: 0.85,
    FitnessGoal.MAINTAIN: 1.0,
    FitnessGoal.GAIN: 1.15,
}


# ========== Формулы BMR ==========

def calculate_bmr_mifflin_st_jeor(gender: Gender, weight_kg: float, height_cm: float, age: int) -> float:
    """Самая точная современная формула"""
    if gender == Gender.MALE:
        bmr = 10 * weight_kg + 6.25 * height_cm - 5 * age + 5
    else:
        bmr = 10 * weight_kg + 6.25 * height_cm - 5 * age - 161
    return max(bmr, 1200)


def calculate_bmr_harris_benedict(gender: Gender, weight_kg: float, height_cm: float, age: int) -> float:
    """Классическая формула 1919 года"""
    if gender == Gender.MALE:
        bmr = 88.362 + (13.397 * weight_kg) + (4.799 * height_cm) - (5.677 * age)
    else:
        bmr = 447.593 + (9.247 * weight_kg) + (3.098 * height_cm) - (4.330 * age)
    return max(bmr, 1200)


def calculate_bmr_katch_mcardle(weight_kg: float, body_fat_percent: float = 15) -> float:
    """Формула на основе сухой массы тела"""
    lean_mass = weight_kg * (1 - body_fat_percent / 100)
    bmr = 370 + (21.6 * lean_mass)
    return max(bmr, 1200)


def calculate_bmr(
    gender: Gender,
    weight_kg: float,
    height_cm: float,
    age: int,
    formula: CalculationFormula = CalculationFormula.MIFFLIN_ST_JEOR,
    body_fat_percent: float = 15
) -> float:
    """Расчёт BMR по выбранной формуле"""
    if formula == CalculationFormula.HARRIS_BENEDICT:
        return calculate_bmr_harris_benedict(gender, weight_kg, height_cm, age)
    elif formula == CalculationFormula.KATCH_MCARDLE:
        return calculate_bmr_katch_mcardle(weight_kg, body_fat_percent)
    else:  # MIFFLIN_ST_JEOR
        return calculate_bmr_mifflin_st_jeor(gender, weight_kg, height_cm, age)


# ========== Макронутриенты ==========

def calculate_macros(calories: float, goal: FitnessGoal = FitnessGoal.MAINTAIN) -> dict[str, float]:
    """
    Расчёт макронутриентов с учётом цели:
    - Похудение: больше белков (35%), меньше углеводов (35%)
    - Поддержание: стандарт (30/25/45)
    - Набор: больше углеводов (50%), стандарт белков (25%)
    """
    if goal == FitnessGoal.LOSE:
        protein_ratio = 0.35
        fat_ratio = 0.30
        carb_ratio = 0.35
    elif goal == FitnessGoal.GAIN:
        protein_ratio = 0.25
        fat_ratio = 0.25
        carb_ratio = 0.50
    else:  # MAINTAIN
        protein_ratio = 0.30
        fat_ratio = 0.25
        carb_ratio = 0.45
    
    proteins = (calories * protein_ratio) / 4
    fats = (calories * fat_ratio) / 9
    carbs = (calories * carb_ratio) / 4
    
    return {
        "calories": calories,
        "proteins": round(proteins, 1),
        "fats": round(fats, 1),
        "carbs": round(carbs, 1),
    }


# ========== Главный расчёт ==========

def calculate_daily_needs(
    gender: Gender,
    age: int,
    weight_kg: float,
    height_cm: float,
    activity_level: ActivityLevel,
    fitness_goal: FitnessGoal = FitnessGoal.MAINTAIN,
    formula: CalculationFormula = CalculationFormula.MIFFLIN_ST_JEOR,
    calorie_adjustment: int = 0,
    body_fat_percent: float = 15
) -> dict[str, float]:
    """Полный расчёт суточной нормы с учётом всех параметров"""
    bmr = calculate_bmr(gender, weight_kg, height_cm, age, formula, body_fat_percent)
    
    multiplier = ACTIVITY_MULTIPLIERS.get(activity_level, 1.55)
    tdee = bmr * multiplier
    
    # Применяем коэффициент цели
    goal_multiplier = GOAL_MULTIPLIERS.get(fitness_goal, 1.0)
    adjusted_calories = tdee * goal_multiplier
    
    # Применяем корректировку
    final_calories = adjusted_calories + calorie_adjustment
    final_calories = max(final_calories, 1200)  # Минимум 1200 ккал
    
    macros = calculate_macros(final_calories, fitness_goal)
    
    return {
        "bmr": round(bmr, 1),
        "tdee": round(tdee, 1),
        "goal_multiplier": goal_multiplier,
        "formula": formula.value,
        "calorie_adjustment": calorie_adjustment,
        **macros,
    }