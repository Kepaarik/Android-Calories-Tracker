import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Header from "../Header";
import { useAuthStore } from "../../../store/authStore";

// Мок CalendarModal чтобы не возиться с порталами
jest.mock("../../ui/CalendarModal", () => ({
  __esModule: true,
  default: () => null,
}));

describe("Header", () => {
  const renderHeader = () =>
    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    );

  beforeEach(() => {
    useAuthStore.setState({ user: null });
  });

  it("показывает 'Дневник' если user не загружен", () => {
    renderHeader();
    expect(screen.getByText("Дневник")).toBeInTheDocument();
  });

  it("показывает first_name если он есть", () => {
    useAuthStore.setState({
      user: {
        id: 1,
        first_name: "Анна",
        username: "anna_k",
        email: null,
        telegram_id: null,
      },
    });

    renderHeader();
    expect(screen.getByText("Анна")).toBeInTheDocument();
  });

  it("показывает username если first_name отсутствует", () => {
    useAuthStore.setState({
      user: {
        id: 1,
        first_name: undefined,
        username: "cool_username",
        email: null,
        telegram_id: null,
      },
    });

    renderHeader();
    expect(screen.getByText("cool_username")).toBeInTheDocument();
  });

  it("рендерит 3 кнопки навигации", () => {
    renderHeader();
    expect(screen.getByTitle("Продукты")).toBeInTheDocument();
    expect(screen.getByTitle("Профиль")).toBeInTheDocument();
    expect(screen.getByTitle("Настройки")).toBeInTheDocument();
  });
});
