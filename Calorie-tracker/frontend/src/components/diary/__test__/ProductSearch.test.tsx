// frontend/src/components/diary/__test__/ProductSearch.test.tsx
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import ProductSearch from "../ProductSearch";
import { productsApi } from "../../../api/endpoints";

vi.mock("../../../api/endpoints", () => ({
  productsApi: {
    getAll: vi.fn(),
  },
}));

const mockProducts = [
  { id: 1, name: "Яблоко", calories: 52, proteins: 0.3, fats: 0.2, carbs: 14 },
  { id: 2, name: "Банан", calories: 89, proteins: 1.1, fats: 0.3, carbs: 23 },
];

describe("ProductSearch", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("отображает поле поиска", () => {
    render(<ProductSearch onSelect={vi.fn()} />);
    expect(
      screen.getByPlaceholderText("Поиск продукта...")
    ).toBeInTheDocument();
  });

  it("не вызывает API при вводе менее 2 символов", async () => {
    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "Я" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(productsApi.getAll).not.toHaveBeenCalled();
    });
  });

  it("вызывает API после debounce (300мс) при 2+ символах", async () => {
    vi.mocked(productsApi.getAll).mockResolvedValue({
      data: mockProducts,
    } as any);

    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "Яб" } });

    // До debounce API не вызван
    expect(productsApi.getAll).not.toHaveBeenCalled();

    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(productsApi.getAll).toHaveBeenCalledWith({ search: "Яб" });
    });
  });

  it("отображает результаты поиска", async () => {
    vi.mocked(productsApi.getAll).mockResolvedValue({
      data: mockProducts,
    } as any);

    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "яб" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(screen.getByText("Яблоко")).toBeInTheDocument();
      expect(screen.getByText("Банан")).toBeInTheDocument();
    });
  });

  it("вызывает onSelect при клике по продукту", async () => {
    vi.mocked(productsApi.getAll).mockResolvedValue({
      data: mockProducts,
    } as any);
    const onSelect = vi.fn();

    render(<ProductSearch onSelect={onSelect} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "яб" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(screen.getByText("Яблоко")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Яблоко"));
    expect(onSelect).toHaveBeenCalledWith(mockProducts[0]);
  });

  it('показывает "Ничего не найдено" при пустом результате', async () => {
    vi.mocked(productsApi.getAll).mockResolvedValue({ data: [] } as any);

    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "несуществующий" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(screen.getByText("Ничего не найдено")).toBeInTheDocument();
    });
  });

  it("показывает ошибку при сбое API", async () => {
    vi.mocked(productsApi.getAll).mockRejectedValue({
      response: { data: { detail: "Ошибка сервера" } },
    });

    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "тест" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(screen.getByText(/Ошибка сервера/)).toBeInTheDocument();
    });
  });

  it("показывает калории рядом с продуктом", async () => {
    vi.mocked(productsApi.getAll).mockResolvedValue({
      data: mockProducts,
    } as any);

    render(<ProductSearch onSelect={vi.fn()} />);
    const input = screen.getByPlaceholderText("Поиск продукта...");

    fireEvent.change(input, { target: { value: "яб" } });
    vi.advanceTimersByTime(300);

    await waitFor(() => {
      expect(screen.getByText("52 ккал")).toBeInTheDocument();
      expect(screen.getByText("89 ккал")).toBeInTheDocument();
    });
  });
});
