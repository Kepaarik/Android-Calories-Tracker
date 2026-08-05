# backend/tests/test_services/test_calorie_calculator.py
import pytest
from app.db.models.user_profile import Gender, ActivityLevel, FitnessGoal, CalculationFormula
from app.services.calorie_calculator import (
    calculate_bmr,
    calculate_bmr_mifflin_st_jeor,
    calculate_bmr_harris_benedict,
    calculate_bmr_katch_mcardle,
    calculate_macros,
    calculate_daily_needs,
    ACTIVITY_MULTIPLIERS,
    GOAL_MULTIPLIERS,
)


class TestCalculateBMRMifflin:
    """Тесты формулы Миффлина-Сан Жеора."""

    def test_male_25_70_175(self):
        # Ожидаемо ~1669 ккал
        bmr = calculate_bmr_mifflin_st_jeor(Gender.MALE, 70, 175, 25)
        assert 1600 < bmr < 1750

    def test_female_25_70_175(self):
        # Ожидаемо ~1498 ккал
        bmr = calculate_bmr_mifflin_st_jeor(Gender.FEMALE, 70, 175, 25)
        assert 1400 < bmr < 1550

    def test_minimum_floor_1200(self):
        # Очень низкие параметры — должно вернуть минимум 1200
        bmr = calculate_bmr_mifflin_st_jeor(Gender.FEMALE, 30, 100, 90)
        assert bmr >= 1200


class TestCalculateBMRHarrisBenedict:
    """Тесты формулы Харриса-Бенедикта."""

    def test_male_25_70_175(self):
        bmr = calculate_bmr_harris_benedict(Gender.MALE, 70, 175, 25)
        assert 1600 < bmr < 1800

    def test_female_25_70_175(self):
        bmr = calculate_bmr_harris_benedict(Gender.FEMALE, 70, 175, 25)
        assert 1400 < bmr < 1600


class TestCalculateBMRKatchMcArdle:
    """Тесты формулы Кэтча-МакАрдла."""

    def test_default_body_fat(self):
        bmr = calculate_bmr_katch_mcardle(70)
        assert bmr > 1200

    def test_low_body_fat_higher_bmr(self):
        bmr_low_fat = calculate_bmr_katch_mcardle(70, body_fat_percent=10)
        bmr_high_fat = calculate_bmr_katch_mcardle(70, body_fat_percent=25)
        assert bmr_low_fat > bmr_high_fat


class TestCalculateBMRDispatcher:
    """Тесты функции-диспетчера calculate_bmr."""

    def test_mifflin_default(self):
        bmr = calculate_bmr(Gender.MALE, 70, 175, 25)
        assert 1600 < bmr < 1750

    def test_harris_benedict(self):
        bmr = calculate_bmr(
            Gender.MALE, 70, 175, 25,
            formula=CalculationFormula.HARRIS_BENEDICT
        )
        assert 1600 < bmr < 1800

    def test_katch_mcardle(self):
        bmr = calculate_bmr(
            Gender.MALE, 70, 175, 25,
            formula=CalculationFormula.KATCH_MCARDLE
        )
        assert bmr > 1200


class TestActivityMultipliers:
    """Тесты коэффициентов активности."""

    @pytest.mark.parametrize(
        "activity,expected",
        [
            (ActivityLevel.SEDENTARY, 1.2),
            (ActivityLevel.LIGHT, 1.375),
            (ActivityLevel.MODERATE, 1.55),
            (ActivityLevel.ACTIVE, 1.725),
            (ActivityLevel.VERY_ACTIVE, 1.9),
        ],
    )
    def test_multipliers(self, activity, expected):
        assert ACTIVITY_MULTIPLIERS[activity] == expected


class TestGoalMultipliers:
    """Тесты коэффициентов целей."""

    @pytest.mark.parametrize(
        "goal,expected",
        [
            (FitnessGoal.LOSE, 0.85),
            (FitnessGoal.MAINTAIN, 1.0),
            (FitnessGoal.GAIN, 1.15),
        ],
    )
    def test_multipliers(self, goal, expected):
        assert GOAL_MULTIPLIERS[goal] == expected


class TestCalculateMacros:
    """Тесты расчёта макронутриентов."""

    def test_maintain_ratios(self):
        macros = calculate_macros(2000, FitnessGoal.MAINTAIN)
        # 30% белки, 25% жиры, 45% углеводы
        assert abs(macros["proteins"] * 4 / 2000 - 0.30) < 0.02
        assert abs(macros["fats"] * 9 / 2000 - 0.25) < 0.02
        assert abs(macros["carbs"] * 4 / 2000 - 0.45) < 0.02

    def test_lose_more_protein(self):
        maintain = calculate_macros(2000, FitnessGoal.MAINTAIN)
        lose = calculate_macros(2000, FitnessGoal.LOSE)
        assert lose["proteins"] > maintain["proteins"]
        assert lose["carbs"] < maintain["carbs"]

    def test_gain_more_carbs(self):
        maintain = calculate_macros(2000, FitnessGoal.MAINTAIN)
        gain = calculate_macros(2000, FitnessGoal.GAIN)
        assert gain["carbs"] > maintain["carbs"]

    def test_sum_approximately_equals_calories(self):
        macros = calculate_macros(2000, FitnessGoal.MAINTAIN)
        total = macros["proteins"] * 4 + macros["fats"] * 9 + macros["carbs"] * 4
        assert abs(total - 2000) < 50

    def test_all_values_positive(self):
        macros = calculate_macros(2000, FitnessGoal.MAINTAIN)
        assert macros["proteins"] > 0
        assert macros["fats"] > 0
        assert macros["carbs"] > 0
        assert macros["calories"] == 2000


class TestCalculateDailyNeeds:
    """Интеграционные тесты полного расчёта."""

    def test_full_calculation_maintain(self):
        result = calculate_daily_needs(
            gender=Gender.MALE,
            age=25,
            weight_kg=70,
            height_cm=175,
            activity_level=ActivityLevel.MODERATE,
            fitness_goal=FitnessGoal.MAINTAIN,
        )
        assert "bmr" in result
        assert "tdee" in result
        assert "calories" in result
        assert result["calories"] > result["bmr"]  # TDEE > BMR
        assert result["calories"] >= 1200

    def test_lose_reduces_calories(self):
        maintain = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.MAINTAIN,
        )
        lose = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.LOSE,
        )
        assert lose["calories"] < maintain["calories"]

    def test_gain_increases_calories(self):
        maintain = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.MAINTAIN,
        )
        gain = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.GAIN,
        )
        assert gain["calories"] > maintain["calories"]

    def test_higher_activity_more_calories(self):
        sedentary = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.SEDENTARY, FitnessGoal.MAINTAIN,
        )
        active = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.ACTIVE, FitnessGoal.MAINTAIN,
        )
        assert active["calories"] > sedentary["calories"]

    def test_calorie_adjustment_applied(self):
        base = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.MAINTAIN,
            calorie_adjustment=0,
        )
        adjusted = calculate_daily_needs(
            Gender.MALE, 25, 70, 175,
            ActivityLevel.MODERATE, FitnessGoal.MAINTAIN,
            calorie_adjustment=200,
        )
        assert abs(adjusted["calories"] - base["calories"] - 200) < 1

    def test_minimum_1200_calories(self):
        # Экстремальные параметры — должно вернуть минимум
        result = calculate_daily_needs(
            Gender.FEMALE, 90, 30, 100,
            ActivityLevel.SEDENTARY, FitnessGoal.LOSE,
            calorie_adjustment=-1000,
        )
        assert result["calories"] >= 1200