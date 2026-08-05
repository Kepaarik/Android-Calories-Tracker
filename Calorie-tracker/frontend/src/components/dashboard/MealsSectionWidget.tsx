// frontend/src/components/dashboard/MealsSectionWidget.tsx
import { useState } from "react";
import { DiaryEntry } from "../../types/api";
import DiaryEntryCard from "../diary/DiaryEntryCard";
import Skeleton from "../ui/Skeleton";
import Icon from "../ui/Icon";
import "./MealsSectionWidget.css";

interface MealsSectionWidgetProps {
  entries: DiaryEntry[];
  isLoading: boolean;
  onDelete: (entry: DiaryEntry) => void;
  onUpdated: () => void;
}

type MealType = "breakfast" | "lunch" | "dinner" | "snack";

// SVG иконки для каждой группы
const MealIcon = ({ type, size = 22 }: { type: MealType; size?: number }) => {
  const icons: Record<MealType, JSX.Element> = {
    breakfast: (
      <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M17 8h1a4 4 0 1 1 0 8h-1" />
        <path d="M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4Z" />
        <line x1="6" y1="2" x2="6" y2="4" />
        <line x1="10" y1="2" x2="10" y2="4" />
        <line x1="14" y1="2" x2="14" y2="4" />
      </svg>
    ),
    lunch: (
      <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M3 11h18" />
        <path d="M3 11a9 9 0 0 1 18 0" />
        <path d="M5 11v2a7 7 0 0 0 14 0v-2" />
        <path d="M8 6c0-1 .5-2 2-2s2 1 2 2" />
        <path d="M14 6c0-1 .5-2 2-2s2 1 2 2" />
      </svg>
    ),
    dinner: (
      <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <circle cx="12" cy="12" r="10" />
        <path d="M12 8v4" />
        <path d="M12 16h.01" />
        <path d="M8 14h8" />
        <path d="M7 10c1-1 3-2 5-2s4 1 5 2" />
      </svg>
    ),
    snack: (
      <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      >
        <path d="M12 2a7 7 0 0 0-7 7c0 3 2 5 3 7s2 4 4 4 3-2 4-4 3-4 3-7a7 7 0 0 0-7-7Z" />
        <path d="M12 2v-1" />
        <path d="M14 3c1-1 2-1 3 0" />
      </svg>
    ),
  };
  return icons[type];
};

const MEAL_CONFIG: Record<MealType, { label: string; color: string }> = {
  breakfast: { label: "Завтрак", color: "var(--warning-color)" },
  lunch: { label: "Обед", color: "var(--macro-protein-color)" },
  dinner: { label: "Ужин", color: "#ab47bc" },
  snack: { label: "Перекус", color: "var(--success-color)" },
};

export default function MealsSectionWidget({
  entries,
  isLoading,
  onDelete,
  onUpdated,
}: MealsSectionWidgetProps) {
  const [expandedMeals, setExpandedMeals] = useState<Set<MealType>>(
    new Set(["breakfast", "lunch", "dinner", "snack"])
  );

  if (isLoading) {
    return (
      <div className="meals-section-widget-skeleton">
        <Skeleton
          variant="text"
          width="30%"
          height="20px"
          className="meals-section-widget-skeleton-title"
        />
        <Skeleton
          variant="card"
          height="100px"
          className="meals-section-widget-skeleton-first-card"
        />
        <Skeleton variant="card" height="100px" />
      </div>
    );
  }

  const groupedEntries: Record<MealType, DiaryEntry[]> = {
    breakfast: [],
    lunch: [],
    dinner: [],
    snack: [],
  };

  entries.forEach((entry) => {
    if (entry.meal_type in groupedEntries) {
      groupedEntries[entry.meal_type as MealType].push(entry);
    }
  });

  const calculateNutrients = (mealEntries: DiaryEntry[]) => {
    let calories = 0;
    let proteins = 0;
    let fats = 0;
    let carbs = 0;

    mealEntries.forEach((entry) => {
      const product = entry.product;
      if (!product) return;
      const weight = entry.weight_grams / 100;
      calories += Number(product.calories) * weight;
      proteins += Number(product.proteins) * weight;
      fats += Number(product.fats) * weight;
      carbs += Number(product.carbs) * weight;
    });

    return {
      calories: Math.round(calories),
      proteins: Math.round(proteins * 10) / 10,
      fats: Math.round(fats * 10) / 10,
      carbs: Math.round(carbs * 10) / 10,
    };
  };

  const toggleMeal = (mealType: MealType) => {
    setExpandedMeals((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(mealType)) {
        newSet.delete(mealType);
      } else {
        newSet.add(mealType);
      }
      return newSet;
    });
  };

  const mealOrder: MealType[] = ["breakfast", "lunch", "dinner", "snack"];

  if (entries.length === 0) {
    return (
      <div
        className="glass card meals-section-widget-empty-card"
      >
        <p className="meals-section-widget-empty-text">
          Нет записей за этот день.
          <br />
          Нажмите «Добавить запись», чтобы начать.
        </p>
      </div>
    );
  }

  return (
    <div className="meals-section-widget-list">
      {mealOrder.map((mealType) => {
        const mealEntries = groupedEntries[mealType];

        if (mealEntries.length === 0) return null;

        const config = MEAL_CONFIG[mealType];
        const nutrients = calculateNutrients(mealEntries);
        const isExpanded = expandedMeals.has(mealType);

        return (
          <div key={mealType} className="meals-section-widget-meal-group">
            <button
              onClick={() => toggleMeal(mealType)}
              className="glass card meals-section-widget-meal-header"
            >
              <div
                className="meals-section-widget-meal-header-left"
              >
                <div
                  className="meals-section-widget-meal-icon"
                  style={{
                    background: `${config.color}20`,
                    color: config.color,
                  }}
                >
                  <MealIcon type={mealType} />
                </div>
                <div className="meals-section-widget-meal-info">
                  <div
                    className="meals-section-widget-meal-label"
                  >
                    {config.label}
                    <span
                      className="meals-section-widget-meal-count"
                    >
                      {mealEntries.length}{" "}
                      {mealEntries.length === 1
                        ? "запись"
                        : mealEntries.length < 5
                        ? "записи"
                        : "записей"}
                    </span>
                  </div>
                  <div
                    className="meals-section-widget-meal-nutrients"
                  >
                    <span className="meals-section-widget-meal-calories">
                      {nutrients.calories} ккал
                    </span>
                    <span className="meals-section-widget-meal-separator">•</span>
                    <span className="meals-section-widget-meal-proteins">
                      Б: {nutrients.proteins}г
                    </span>
                    <span className="meals-section-widget-meal-fats">
                      Ж: {nutrients.fats}г
                    </span>
                    <span className="meals-section-widget-meal-carbs">
                      У: {nutrients.carbs}г
                    </span>
                  </div>
                </div>
              </div>

              <div
                className="meals-section-widget-meal-chevron"
                style={{
                  transform: isExpanded ? "rotate(180deg)" : "rotate(0deg)",
                }}
              >
                <Icon name="chevron-down" size={20} />
              </div>
            </button>

            {isExpanded && (
              <div
                className="meals-section-widget-meal-entries"
                style={{
                  borderLeft: `2px solid ${config.color}`,
                }}
              >
                {mealEntries.map((entry) => (
                  <DiaryEntryCard
                    key={entry.id}
                    entry={entry}
                    onDelete={onDelete}
                    onUpdated={onUpdated}
                  />
                ))}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}
