// frontend/src/components/dashboard/__tests__/DailySummaryWidget.test.tsx
import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import DailySummaryWidget from "../DailySummaryWidget";

const mockSummary = {
  calories: 1500,
  proteins: 120,
  fats: 50,
  carbs: 200,
  entries_count: 3,
};

describe("DailySummaryWidget", () => {
  it("отображает заголовок и цель", () => {
    render(
      <DailySummaryWidget summary={mockSummary} dailyCalorieGoal={2000} />
    );
    expect(screen.getByText("Сводка за день")).toBeInTheDocument();
    expect(screen.getByText(/Цель: 2000 ккал/)).toBeInTheDocument();
  });

  it("отображает количество съеденных ккал", () => {
    render(
      <DailySummaryWidget summary={mockSummary} dailyCalorieGoal={2000} />
    );
    // Проверяем, что число 1500 есть на экране (может быть внутри span/div)
    expect(screen.getByText("1500")).toBeInTheDocument();
  });

  it("отображает значения БЖУ", () => {
    render(
      <DailySummaryWidget summary={mockSummary} dailyCalorieGoal={2000} />
    );
    expect(screen.getByText("120")).toBeInTheDocument(); // Белки
    expect(screen.getByText("50")).toBeInTheDocument(); // Жиры
    expect(screen.getByText("200")).toBeInTheDocument(); // Углеводы
  });
});
