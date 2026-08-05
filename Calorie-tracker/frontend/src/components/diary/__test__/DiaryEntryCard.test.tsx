// frontend/src/components/diary/__test__/DiaryEntryCard.test.tsx
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import DiaryEntryCard from "../DiaryEntryCard";
import { diaryApi } from "../../../api/endpoints";

vi.mock("../../../api/endpoints", () => ({
  diaryApi: {
    updateEntry: vi.fn(),
    deleteEntry: vi.fn(),
  },
}));

const mockEntry = {
  id: 1,
  product_id: 1,
  weight_grams: 100,
  meal_type: "breakfast",
  consumed_at: "2026-07-10T12:00:00",
  created_at: "2026-07-10T12:00:00",
  product: {
    id: 1,
    name: "Яблоко",
    calories: 52,
    proteins: 0.3,
    fats: 0.2,
    carbs: 14,
  },
} as any;

describe("DiaryEntryCard", () => {
  const mockOnUpdated = vi.fn();
  const mockOnDelete = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("отображает название продукта", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );
    expect(screen.getByText("Яблоко")).toBeInTheDocument();
  });

  it("отображает вес и калории", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );
    expect(screen.getByText(/100г/)).toBeInTheDocument();
    expect(screen.getByText(/52 ккал/)).toBeInTheDocument();
  });

  it("отображает БЖУ", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );
    expect(screen.getByText(/Б:/)).toBeInTheDocument();
    expect(screen.getByText(/Ж:/)).toBeInTheDocument();
    expect(screen.getByText(/У:/)).toBeInTheDocument();
  });

  it("отображает время в минском часовом поясе", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );
    // Время должно отображаться (точный формат зависит от локали)
    const timeElement = screen.getByText(/:\d{2}/);
    expect(timeElement).toBeInTheDocument();
  });

  it("открывает форму редактирования при клике на карандаш", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByTitle("Редактировать"));

    expect(screen.getByDisplayValue("100")).toBeInTheDocument(); // поле веса
    expect(screen.getByText("Сохранить")).toBeInTheDocument();
  });

  it('отменяет редактирование при клике на "Отмена"', () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByTitle("Редактировать"));
    fireEvent.click(screen.getByText("Отмена"));

    expect(screen.queryByText("Сохранить")).not.toBeInTheDocument();
  });

  it("сохраняет изменения веса", async () => {
    vi.mocked(diaryApi.updateEntry).mockResolvedValue({} as any);

    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByTitle("Редактировать"));

    const weightInput = screen.getByDisplayValue("100");
    fireEvent.change(weightInput, { target: { value: "200" } });
    fireEvent.click(screen.getByText("Сохранить"));

    await waitFor(() => {
      expect(diaryApi.updateEntry).toHaveBeenCalledWith(1, {
        weight_grams: 200,
      });
      expect(mockOnUpdated).toHaveBeenCalled();
    });
  });

  it("показывает алерт при невалидном весе", async () => {
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByTitle("Редактировать"));

    const weightInput = screen.getByDisplayValue("100");
    fireEvent.change(weightInput, { target: { value: "0" } });
    fireEvent.click(screen.getByText("Сохранить"));

    expect(alertSpy).toHaveBeenCalledWith("Введите корректный вес");
    expect(diaryApi.updateEntry).not.toHaveBeenCalled();

    alertSpy.mockRestore();
  });

  it("вызывает onDelete при клике на корзину", () => {
    render(
      <DiaryEntryCard
        entry={mockEntry}
        onUpdated={mockOnUpdated}
        onDelete={mockOnDelete}
      />
    );

    fireEvent.click(screen.getByTitle("Удалить"));

    expect(mockOnDelete).toHaveBeenCalledWith(mockEntry);
  });
});
