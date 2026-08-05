import { renderHook, waitFor } from "@testing-library/react";
import { useTelegramAuth } from "../useTelegramAuth";
import { useAuthStore } from "../../store/authStore";
import { apiClient } from "../../api/client";
import WebApp from "@twa-dev/sdk";

// Моки
jest.mock("@twa-dev/sdk");
jest.mock("../../api/client", () => ({
  apiClient: {
    post: jest.fn(),
    get: jest.fn(),
  },
}));

const mockedWebApp = WebApp as jest.Mocked<typeof WebApp>;
const mockedApiClient = apiClient as jest.Mocked<typeof apiClient>;

describe("useTelegramAuth", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Сбрасываем store перед каждым тестом
    useAuthStore.setState({
      token: null,
      user: null,
      isAuthenticated: false,
      isLoading: false,
    });
    localStorage.clear();
  });

  describe("Вне Telegram", () => {
    it("пропускает авто-вход если нет initData", async () => {
      mockedWebApp.initData = "";

      const { result } = renderHook(() => useTelegramAuth());

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      expect(mockedApiClient.post).not.toHaveBeenCalled();
      expect(result.current.authError).toBeNull();
    });
  });

  describe("В Telegram с initData", () => {
    beforeEach(() => {
      mockedWebApp.initData = "valid_init_data_string";
    });

    it("успешный авто-вход сохраняет token и user", async () => {
      mockedApiClient.post.mockResolvedValueOnce({
        data: {
          access_token: "test_token_123",
          user: {
            id: 1,
            telegram_id: 123456,
            first_name: "Иван",
            username: "ivan",
          },
        },
      });

      const { result } = renderHook(() => useTelegramAuth());

      // Сначала isLoading = true
      expect(result.current.isAuthenticating).toBe(true);

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      expect(mockedApiClient.post).toHaveBeenCalledWith(
        "/api/auth/telegram-login",
        {},
        { headers: { "X-Telegram-Init-Data": "valid_init_data_string" } }
      );

      // Проверяем store
      const state = useAuthStore.getState();
      expect(state.token).toBe("test_token_123");
      expect(state.user?.first_name).toBe("Иван");
      expect(result.current.authError).toBeNull();
    });

    it("показывает понятную ошибку при 401 с expired", async () => {
      mockedApiClient.post.mockRejectedValueOnce({
        response: {
          status: 401,
          data: { detail: "Init data expired" },
        },
      });

      const { result } = renderHook(() => useTelegramAuth());

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      expect(result.current.authError).toContain("истекла");
    });

    it("показывает ошибку при 409 (занятый Telegram)", async () => {
      mockedApiClient.post.mockRejectedValueOnce({
        response: {
          status: 409,
          data: { detail: "Telegram already bound" },
        },
      });

      const { result } = renderHook(() => useTelegramAuth());

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      expect(result.current.authError).toContain("уже привязан");
    });

    it("показывает ошибку сети при отсутствии response", async () => {
      mockedApiClient.post.mockRejectedValueOnce({
        message: "Network Error",
        // Нет response - это сетевая ошибка
      });

      const { result } = renderHook(() => useTelegramAuth());

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      expect(result.current.authError).toContain("подключени");
    });

    it("не пытается войти повторно если уже авторизован", async () => {
      // Пользователь уже авторизован
      useAuthStore.setState({
        token: "existing_token",
        isAuthenticated: true,
      });

      mockedApiClient.get.mockResolvedValueOnce({
        data: { id: 1, first_name: "Иван" },
      });

      const { result } = renderHook(() => useTelegramAuth());

      await waitFor(() => {
        expect(result.current.isAuthenticating).toBe(false);
      });

      // telegram-login НЕ должен вызываться
      expect(mockedApiClient.post).not.toHaveBeenCalled();
      // Но должен вызваться loadUser через get /api/auth/me
      expect(mockedApiClient.get).toHaveBeenCalledWith("/api/auth/me");
    });
  });
});
