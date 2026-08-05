import { create } from "zustand";
import { User } from "../types/auth";
import { apiClient } from "../api/client";

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  setToken: (token: string) => void;
  setUser: (user: User) => void;
  loadUser: () => Promise<void>;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem("token"),
  user: null,
  isAuthenticated: !!localStorage.getItem("token"),
  isLoading: false,

  setToken: (token) => {
    localStorage.setItem("token", token);
    set({ token, isAuthenticated: true });
  },

  setUser: (user) => set({ user }),

  loadUser: async () => {
    const token = get().token;
    if (!token || get().user) return; // уже есть user или нет токена

    set({ isLoading: true });
    try {
      const res = await apiClient.get("/api/auth/me");
      set({ user: res.data });
    } catch (err) {
      console.error("Failed to load user:", err);
      // Если токен невалиден - выходим
      if ((err as any)?.response?.status === 401) {
        get().logout();
      }
    } finally {
      set({ isLoading: false });
    }
  },

  logout: () => {
    localStorage.removeItem("token");
    set({ token: null, user: null, isAuthenticated: false });
  },
}));

// Автоматически загружаем user при наличии токена
if (localStorage.getItem("token")) {
  useAuthStore.getState().loadUser();
}
