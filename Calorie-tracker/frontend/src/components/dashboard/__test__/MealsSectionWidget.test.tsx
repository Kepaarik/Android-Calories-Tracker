// frontend/src/components/dashboard/__test__/MealsSectionWidget.test.tsx
import { render, screen, fireEvent } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import MealsSectionWidget from "../MealsSectionWidget";
import { DiaryEntry } from "../../../types/api";

const mockEntries: DiaryEntry[] = [
  {
    id: 1,
    product: {
      id: 1,
      name: "Яблоко",
      calories: 52,
      proteins: 0.3,
      fats: 0.2,
      carbs: 14,
    },
    weight_grams: 100,
    meal_type: "breakfast",
    consumed_at: "2024-01-15T08:00:00Z",
  } as any,
  {
    id: 2,
    product: {
      id: 2,
      name: "Банан",
      calories: 89,
      proteins: 1.1,
      fats: 0.3,
      carbs: 23,
    },
    weight_grams: 200,
    meal_type: "breakfast",
    consumed_at: "2024-01-15T08:30:00Z",
  } as any,
];

describe("MealsSectionWidget", () => {
  it("отображает сообщение, если нет записей", () => {
    render(
      <MealsSectionWidget
        entries={[]}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    expect(screen.getByText(/Нет записей за этот день/)).toBeInTheDocument();
  });

  it("отображает скелетон при загрузке", () => {
    const { container } = render(
      <MealsSectionWidget
        entries={[]}
        isLoading={true}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    expect(container.querySelector(".skeleton")).toBeInTheDocument();
  });

  it('отображает заголовок группы "Завтрак"', () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    expect(screen.getByText("Завтрак")).toBeInTheDocument();
    expect(screen.getByText(/2 записи/)).toBeInTheDocument();
  });

  it("не показывает пустые группы", () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    expect(screen.queryByText("Обед")).not.toBeInTheDocument();
    expect(screen.queryByText("Ужин")).not.toBeInTheDocument();
    expect(screen.queryByText("Перекус")).not.toBeInTheDocument();
  });

  it("скрывает продукты по умолчанию (аккордеон свёрнут)", () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    expect(screen.queryByText("Яблоко")).not.toBeInTheDocument();
    expect(screen.queryByText("Банан")).not.toBeInTheDocument();
  });

  it("раскрывает список при клике на заголовок", () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );

    const header = screen.getByText("Завтрак");
    fireEvent.click(header);

    expect(screen.getByText("Яблоко")).toBeInTheDocument();
    expect(screen.getByText("Банан")).toBeInTheDocument();
  });

  it("сворачивает список при повторном клике", () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );

    const header = screen.getByText("Завтрак");
    fireEvent.click(header); // открыть
    expect(screen.getByText("Яблоко")).toBeInTheDocument();

    fireEvent.click(header); // закрыть
    expect(screen.queryByText("Яблоко")).not.toBeInTheDocument();
  });

  it("считает КБЖУ для группы", () => {
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={vi.fn()}
        onUpdated={vi.fn()}
      />
    );
    // 100г яблока (52 ккал) + 200г банана (89*2=178 ккал) = 230 ккал
    expect(screen.getByText("230 ккал")).toBeInTheDocument();
  });

  it("вызывает onDelete при клике на кнопку удаления внутри раскрытой группы", () => {
    const onDelete = vi.fn();
    render(
      <MealsSectionWidget
        entries={mockEntries}
        isLoading={false}
        onDelete={onDelete}
        onUpdated={vi.fn()}
      />
    );

    // Сначала раскрываем группу
    fireEvent.click(screen.getByText("Завтрак"));

    // Теперь кликаем по кнопке удаления
    const deleteButtons = screen.getAllByTitle("Удалить");
    fireEvent.click(deleteButtons[0]);

    expect(onDelete).toHaveBeenCalledWith(mockEntries[0]);
  });
});
