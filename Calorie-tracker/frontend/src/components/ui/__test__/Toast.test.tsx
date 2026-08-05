// frontend/src/components/ui/__test__/Toast.test.tsx
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import Toast from "../Toast";

describe("Toast", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("отображает сообщение", () => {
    render(<Toast message="Запись удалена" onClose={vi.fn()} />);
    expect(screen.getByText("Запись удалена")).toBeInTheDocument();
  });

  it("отображает subtitle если передан", () => {
    render(
      <Toast message="Запись удалена" subtitle="Яблоко" onClose={vi.fn()} />
    );
    expect(screen.getByText("Яблоко")).toBeInTheDocument();
  });

  it('не отображает кнопку "Отменить" если onUndo не передан', () => {
    render(<Toast message="Тест" onClose={vi.fn()} />);
    expect(screen.queryByText("Отменить")).not.toBeInTheDocument();
  });

  it('отображает кнопку "Отменить" если передан onUndo', () => {
    render(
      <Toast message="Запись удалена" onUndo={vi.fn()} onClose={vi.fn()} />
    );
    expect(screen.getByText("Отменить")).toBeInTheDocument();
  });

  it('вызывает onUndo при клике на кнопку "Отменить"', () => {
    const onUndo = vi.fn();
    render(
      <Toast message="Запись удалена" onUndo={onUndo} onClose={vi.fn()} />
    );

    fireEvent.click(screen.getByText("Отменить"));
    expect(onUndo).toHaveBeenCalled();
  });

  it("вызывает onClose после истечения duration", async () => {
    const onClose = vi.fn();
    render(<Toast message="Тест" onClose={onClose} duration={3000} />);

    vi.advanceTimersByTime(3000);

    await waitFor(() => {
      expect(onClose).toHaveBeenCalled();
    });
  });

  it("вызывает onClose при клике на крестик", () => {
    const onClose = vi.fn();
    render(<Toast message="Тест" onClose={onClose} />);

    // Клик по иконке close (используем role="button" для SVG)
    const closeButton = screen
      .getAllByRole("button")
      .find((btn) => !btn.textContent?.includes("Отменить"));
    fireEvent.click(closeButton!);

    expect(onClose).toHaveBeenCalled();
  });
});
