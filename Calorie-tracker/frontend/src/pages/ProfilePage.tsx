import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import WebApp from "@twa-dev/sdk"; // ← ДОБАВЛЕНО
import { profileApi } from "../api/endpoints";
import { apiClient } from "../api/client"; // ← ДОБАВЛЕНО
import {
  UserProfileResponse,
  Gender,
  ActivityLevel,
  FitnessGoal,
  CalculationFormula,
  UserProfileCreate,
} from "../types/api";
import PageHeader from "../components/ui/PageHeader";
import GlassCard from "../components/ui/GlassCard";
import GlassButton from "../components/ui/GlassButton";
import GenderSelector from "./profile/GenderSelector";
import ActivitySelector from "./profile/ActivitySelector";
import GoalSelector from "./profile/GoalSelector";
import CalorieAdjuster from "./profile/CalorieAdjuster";
import ProfilePreview from "./profile/ProfilePreview";
import {
  ACTIVITY_LEVELS,
  FITNESS_GOALS,
  CALCULATION_FORMULAS,
} from "./profile/constants";
import Skeleton from "../components/ui/Skeleton";
import { useToast } from "../context/ToastContext";
import { useAuthStore } from "../store/authStore"; // ← ДОБАВЛЕНО
import CustomSelect from "../components/ui/CustomSelect";
import ThemeToggle from "../components/ui/ThemeToggle";
import AccentColorPicker from "../components/ui/AccentColorPicker";
import "./ProfilePage.css";

export default function ProfilePage() {
  const toast = useToast();
  const navigate = useNavigate();
  const { user } = useAuthStore(); // ← ДОБАВЛЕНО
  const [profile, setProfile] = useState<UserProfileResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isBinding, setIsBinding] = useState(false); // ← ДОБАВЛЕНО
  const [isUnbinding, setIsUnbinding] = useState(false);
  const [gender, setGender] = useState<Gender>("male");
  const [age, setAge] = useState("25");
  const [weight, setWeight] = useState("70");
  const [height, setHeight] = useState("175");
  const [activityLevel, setActivityLevel] = useState<ActivityLevel>("moderate");
  const [fitnessGoal, setFitnessGoal] = useState<FitnessGoal>("maintain");
  const [formula, setFormula] = useState<CalculationFormula>("mifflin_st_jeor");
  const [adjustment, setAdjustment] = useState(0);

  const [preview, setPreview] = useState({
    calories: 2000,
    proteins: 150,
    fats: 56,
    carbs: 225,
  });

  useEffect(() => {
    loadProfile();
  }, []);

  useEffect(() => {
    const timer = setTimeout(async () => {
      try {
        const res = await profileApi.calculatePreview({
          gender,
          age: parseInt(age) || 25,
          weight_kg: parseFloat(weight) || 70,
          height_cm: parseFloat(height) || 175,
          activity_level: activityLevel,
          fitness_goal: fitnessGoal,
          calculation_formula: formula,
          calorie_adjustment: adjustment,
        });
        setPreview({
          calories: Math.round(res.data.calories),
          proteins: parseFloat(res.data.proteins.toFixed(1)),
          fats: parseFloat(res.data.fats.toFixed(1)),
          carbs: parseFloat(res.data.carbs.toFixed(1)),
        });
      } catch {
        // игнорируем ошибки валидации при вводе
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [
    gender,
    age,
    weight,
    height,
    activityLevel,
    fitnessGoal,
    formula,
    adjustment,
  ]);

  const loadProfile = async () => {
    try {
      const res = await profileApi.getProfile();
      const d = res.data;
      setProfile(d);
      setGender(d.gender);
      setAge(String(d.age));
      setWeight(String(d.weight_kg));
      setHeight(String(d.height_cm));
      setActivityLevel(d.activity_level);
      setFitnessGoal(d.fitness_goal);
      setFormula(d.calculation_formula);
      setAdjustment(d.calorie_adjustment);
    } catch (e: any) {
      if (e.response?.status !== 404) console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    const data: UserProfileCreate = {
      gender,
      age: parseInt(age),
      weight_kg: parseFloat(weight),
      height_cm: parseFloat(height),
      activity_level: activityLevel,
      fitness_goal: fitnessGoal,
      calculation_formula: formula,
      calorie_adjustment: adjustment,
    };
    try {
      if (profile) await profileApi.updateProfile(data);
      else await profileApi.createProfile(data);
      navigate("/");
    } catch (e: any) {
      toast.error(e.response?.data?.detail || "Ошибка сохранения");
    } finally {
      setIsSaving(false);
    }
  };
  const { setUser } = useAuthStore.getState();

  const handleUnbindTelegram = async () => {
    if (!confirm("Вы уверены, что хотите отвязать Telegram?")) return;

    setIsUnbinding(true);
    try {
      await apiClient.post("/api/auth/unbind-telegram");

      // Обновляем user в store
      const userRes = await apiClient.get("/api/auth/me");
      setUser(userRes.data);

      toast.success("Telegram успешно отвязан");
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка отвязки");
    } finally {
      setIsUnbinding(false);
    }
  };

  // ← НОВАЯ ФУНКЦИЯ: Привязка Telegram
  const handleBindTelegram = async () => {
    if (!WebApp.initData) {
      toast.warning("Откройте приложение через Telegram для привязки");
      return;
    }
    setIsBinding(true);
    try {
      const res = await apiClient.post(
        "/api/auth/bind-telegram",
        {},
        {
          headers: { "X-Telegram-Init-Data": WebApp.initData },
        }
      );

      // ← ДОБАВЛЕНО: обновляем user в store после привязки
      const userRes = await apiClient.get("/api/auth/me");
      setUser(userRes.data);

      toast.success("Telegram успешно привязан!");
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка привязки Telegram");
    } finally {
      setIsBinding(false);
    }
  };

  if (isLoading) {
    return (
      <div className="container profile-page-container">
        <div
          className="glass card profile-page-skeleton-header"
        >
          <div className="profile-page-skeleton-header-row">
            <Skeleton
              variant="rect"
              width="40px"
              height="40px"
              style={{ borderRadius: "50%" }}
            />
            <div className="profile-page-skeleton-name-block">
              <Skeleton variant="text" width="40%" height="24px" />
              <Skeleton
                variant="text"
                width="25%"
                height="16px"
                style={{ marginTop: "8px" }}
              />
            </div>
            <Skeleton
              variant="rect"
              width="40px"
              height="40px"
              style={{ borderRadius: "50%" }}
            />
          </div>
        </div>
        <div
          className="glass card profile-page-skeleton-form"
        >
          <Skeleton
            variant="text"
            width="30%"
            height="20px"
            style={{ marginBottom: "16px" }}
          />
          <div className="profile-page-skeleton-field">
            <Skeleton
              variant="text"
              width="20%"
              height="12px"
              style={{ marginBottom: "8px" }}
            />
            <div className="profile-page-skeleton-field-row">
              <Skeleton variant="rect" width="50%" height="48px" />
              <Skeleton variant="rect" width="50%" height="48px" />
            </div>
          </div>
          <div
            className="profile-page-skeleton-grid"
          >
            {[1, 2, 3].map((i) => (
              <div key={i}>
                <Skeleton
                  variant="text"
                  width="60%"
                  height="12px"
                  style={{ marginBottom: "6px" }}
                />
                <Skeleton variant="rect" width="100%" height="40px" />
              </div>
            ))}
          </div>
          <Skeleton variant="rect" width="100%" height="52px" />
        </div>
      </div>
    );
  }

  const isTelegramBound = !!user?.telegram_id;
  const isInTelegram = !!(WebApp.initData && WebApp.initData.length > 0);

  return (
    <div className="container profile-page-container">
      <PageHeader title="Настройки профиля" onBack={() => navigate(-1)} />

      <GlassCard className="profile-page-theme-card">
        <h3 className="profile-page-theme-title">
          Тема оформления
        </h3>
        <ThemeToggle />

        <h3 className="profile-page-theme-title profile-page-accent-title">
          Основной цвет
        </h3>
        <AccentColorPicker />
      </GlassCard>

      {/* Секция привязки Telegram */}
      <GlassCard className="profile-page-telegram-card" padding="20px">
        <h3 className="profile-page-telegram-title">

          {/* Иконка ссылки */}
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          >
            <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path>
            <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path>
          </svg>
          Привязка Telegram
        </h3>

        {user?.telegram_id ? (
          // Telegram привязан
          <div>
            <div
              className="profile-page-telegram-bound-banner"
            >
              {/* Иконка галочки в круге */}
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#4caf50"
                strokeWidth="2"
              >
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              <div className="profile-page-telegram-bound-text">
                <div
                  className="profile-page-telegram-bound-title"
                >
                  Telegram привязан
                </div>
                <div
                  className="profile-page-telegram-bound-subtitle"
                >
                  Аккаунт связан с{" "}
                  <strong>{user.username || `ID ${user.telegram_id}`}</strong>
                </div>
              </div>
            </div>

            <GlassButton
              variant="danger"
              fullWidth
              onClick={handleUnbindTelegram}
              disabled={isUnbinding}
              className="profile-page-telegram-action-btn"
            >
              {/* Иконка разорванной цепи */}
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              >
                <path d="M18.84 12.25l1.72-1.71a5 5 0 0 0-7.07-7.07l-3 3a5 5 0 0 0 .54 7.54"></path>
                <path d="M5.16 11.75l-1.72 1.71a5 5 0 0 0 7.07 7.07l3-3a5 5 0 0 0-.54-7.54"></path>
                <line x1="2" y1="2" x2="22" y2="22"></line>
              </svg>
              {isUnbinding ? "Отвязка..." : "Отвязать Telegram"}
            </GlassButton>
          </div>
        ) : isInTelegram ? (
          // Telegram не привязан, но мы в TG
          <>
            <p
              className="profile-page-telegram-hint"
            >
              Привяжите текущий аккаунт Telegram для автоматического входа через
              Mini App без ввода пароля.
            </p>
            <GlassButton
              variant="success"
              fullWidth
              onClick={handleBindTelegram}
              disabled={isBinding}
              className="profile-page-telegram-bind-btn"
            >
              {/* Иконка цепи (привязка) */}
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
              >
                <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"></path>
                <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"></path>
              </svg>
              {isBinding ? "Привязка..." : "Привязать текущий Telegram"}
            </GlassButton>
          </>
        ) : (
          // Мы не в Telegram
          <div
            className="profile-page-telegram-not-in-app"
          >
            {/* Иконка предупреждения */}
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
              <line x1="12" y1="9" x2="12" y2="13"></line>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
            Откройте приложение через Telegram Mini App, чтобы привязать аккаунт
          </div>
        )}
      </GlassCard>

      {/* Форма профиля (без изменений) */}
      <form onSubmit={handleSubmit}>
        <GlassCard className="profile-page-form-card" padding="20px">
          <h3
            className="profile-page-form-section-title"
          >
            Основные данные
          </h3>

          <label className="input-label">Пол</label>
          <GenderSelector selected={gender} onChange={setGender} />

          <div
            className="profile-page-numeric-fields-grid"
          >
            {[
              {
                label: "Возраст",
                value: age,
                setter: setAge,
                min: 10,
                max: 120,
              },
              {
                label: "Вес (кг)",
                value: weight,
                setter: setWeight,
                min: 30,
                max: 300,
                step: "0.1",
              },
              {
                label: "Рост (см)",
                value: height,
                setter: setHeight,
                min: 100,
                max: 250,
              },
            ].map((f) => (
              <div key={f.label}>
                <label className="input-label">{f.label}</label>
                <input
                  type="number"
                  className="glass-input profile-page-numeric-input"
                  value={f.value}
                  onChange={(e) => f.setter(e.target.value)}
                  min={f.min}
                  max={f.max}
                  step={f.step}
                  required
                />
              </div>
            ))}
          </div>

          <CustomSelect<CalculationFormula>
            label="Формула расчёта"
            value={formula}
            onChange={(val) => setFormula(val)}
            options={CALCULATION_FORMULAS.map((f) => ({
              value: f.value,
              label: f.label,
              description: f.description,
            }))}
          />

          <p
            className="profile-page-formula-description"
          >
            {CALCULATION_FORMULAS.find((f) => f.value === formula)?.description}
          </p>

          <label className="input-label profile-page-section-label">
            Уровень активности
          </label>
          <ActivitySelector
            levels={ACTIVITY_LEVELS}
            selected={activityLevel}
            onChange={setActivityLevel}
          />

          <label className="input-label profile-page-section-label">
            Цель
          </label>
          <GoalSelector
            goals={FITNESS_GOALS}
            selected={fitnessGoal}
            onChange={setFitnessGoal}
          />

          <label className="input-label profile-page-section-label">
            Корректировка калорий
          </label>
          <CalorieAdjuster
            value={adjustment}
            onChange={setAdjustment}
            step={50}
          />
        </GlassCard>

        <ProfilePreview {...preview} />

        <GlassButton
          type="submit"
          variant="success"
          fullWidth
          className="profile-page-submit-btn"
          disabled={isSaving}
        >
          {isSaving ? "Сохранение..." : "Сохранить профиль"}
        </GlassButton>
      </form>
    </div>
  );
}
