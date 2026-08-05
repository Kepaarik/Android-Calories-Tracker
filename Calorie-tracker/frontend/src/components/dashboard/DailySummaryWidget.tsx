import { DailySummary } from "../../types/api";
import StatCard from "../ui/StatCard";
import Skeleton from "../ui/Skeleton";
import "./DailySummaryWidget.css";

interface DailySummaryWidgetProps {
  summary: DailySummary;
  dailyCalorieGoal: number;
  isLoading?: boolean;
}

export default function DailySummaryWidget({
  summary,
  dailyCalorieGoal,
  isLoading = false,
}: DailySummaryWidgetProps) {
  // Безопасное извлечение значений с fallback
  const calories = summary.calories ?? summary.total_calories ?? 0;
  const proteins = summary.proteins ?? summary.total_proteins ?? 0;
  const fats = summary.fats ?? summary.total_fats ?? 0;
  const carbs = summary.carbs ?? summary.total_carbs ?? 0;

  const caloriesLeft = Math.round(Math.max(dailyCalorieGoal - calories, 0));

  if (isLoading || !summary) {
    return (
      <div
        className="glass card daily-summary-card"
      >
        <div
          className="daily-summary-header"
        >
          <Skeleton variant="text" width="120px" height="24px" />
          <Skeleton variant="text" width="100px" height="18px" />
        </div>

        {/* Скелетон прогресс-бара */}
        <Skeleton
          variant="rect"
          height="12px"
          className="daily-summary-skeleton-progress"
        />

        {/* Скелетон сетки КБЖУ */}
        <div className="stats-grid">
          {[1, 2, 3, 4].map((i) => (
            <Skeleton key={i} variant="card" height="80px" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div
      className="glass card daily-summary-card"
    >
      <div
        className="daily-summary-header"
      >
        <h3
          className="daily-summary-title"
        >
          Сводка за день
        </h3>
        <span className="daily-summary-goal">
          Цель: {Math.round(dailyCalorieGoal)} ккал
        </span>
      </div>

      {/* Многоцветная шкала */}
      <div className="daily-summary-progress-section">
        <div
          className="progress-bar daily-summary-progress-bar"
        >
          <div
            className="daily-summary-progress-segment daily-summary-progress-protein"
            style={{
              width: `${dailyCalorieGoal > 0 ? ((proteins * 4) / dailyCalorieGoal) * 100 : 0}%`,
              minWidth: proteins > 0 ? "2px" : "0",
            }}
          />
          <div
            className="daily-summary-progress-segment daily-summary-progress-fat"
            style={{
              width: `${dailyCalorieGoal > 0 ? ((fats * 9) / dailyCalorieGoal) * 100 : 0}%`,
              minWidth: fats > 0 ? "2px" : "0",
            }}
          />
          <div
            className="daily-summary-progress-segment daily-summary-progress-carb"
            style={{
              width: `${dailyCalorieGoal > 0 ? ((carbs * 4) / dailyCalorieGoal) * 100 : 0}%`,
              minWidth: carbs > 0 ? "2px" : "0",
            }}
          />
        </div>
        <div
          className="daily-summary-progress-footer"
        >
          <span>{Math.round(calories)} ккал съедено</span>
          <span>{caloriesLeft} осталось</span>
        </div>
        <div
          className="daily-summary-legend"
        >
          <span className="daily-summary-legend-protein">■ Белки</span>
          <span className="daily-summary-legend-fat">■ Жиры</span>
          <span className="daily-summary-legend-carb">■ Углеводы</span>
        </div>
      </div>

      {/* Сетка КБЖУ */}
      <div className="stats-grid">
        <StatCard
          value={Math.round(calories)}
          label="ккал"
          color="var(--danger-color)"
        />
        <StatCard
          value={Math.round(proteins)}
          label="белки"
          color="var(--macro-protein-color)"
        />
        <StatCard
          value={Math.round(fats)}
          label="жиры"
          color="var(--warning-color)"
        />
        <StatCard
          value={Math.round(carbs)}
          label="углеводы"
          color="var(--success-color)"
        />
      </div>
    </div>
  );
}