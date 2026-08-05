// frontend/src/api/client.ts
import axios from "axios";
import WebApp from "@twa-dev/sdk";
import { useAuthStore } from "../store/authStore";

const API_BASE_URL = import.meta.env.VITE_API_URL || "https://calorie.danon4ik.cc";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// ЕДИНСТВЕННЫЙ интерсептор авторизации
apiClient.interceptors.request.use((config) => {
  // EDGE-CASE: WebApp.initData остаётся truthy на весь сеанс внутри Telegram,
  // поэтому его наличие не должно блокировать обычную Bearer-авторизацию —
  // X-Telegram-Init-Data нужен только для /telegram-login и /bind-telegram,
  // которые сами выставляют этот заголовок при вызове
  const tokenFromStore = useAuthStore.getState().token;
  const tokenFromStorage = localStorage.getItem("token");
  const token = tokenFromStore || tokenFromStorage;

  if (token && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Интерсептор ответов
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response) {
      window.dispatchEvent(
        new CustomEvent("global-toast", {
          detail: { message: "Нет подключения к интернету", type: "error" },
        })
      );
    } else if (error.response.status === 401) {
      localStorage.removeItem("token");
      if (!WebApp.initData) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);