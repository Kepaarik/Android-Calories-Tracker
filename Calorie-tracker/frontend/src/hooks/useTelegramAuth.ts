import { useEffect, useRef, useState } from "react";
import WebApp from "@twa-dev/sdk";
import { useAuthStore } from "../store/authStore";
import { apiClient } from "../api/client";

export const useTelegramAuth = () => {
  const [isAuthenticating, setIsAuthenticating] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);
  const { setToken, setUser, isAuthenticated, loadUser } = useAuthStore();
  const hasRunRef = useRef(false);

  useEffect(() => {
    // EDGE-CASE: React.StrictMode дважды монтирует эффекты в dev-режиме —
    // без этой защиты initAuth() отправлял бы два одновременных запроса на вход
    if (hasRunRef.current) return;
    hasRunRef.current = true;

    const initAuth = async () => {
      // Не в Telegram — пропускаем авто-вход
      if (!WebApp.initData || WebApp.initData.length === 0) {
        setIsAuthenticating(false);
        return;
      }

      // Уже авторизован — просто загружаем свежие данные
      if (isAuthenticated) {
        try {
          await loadUser();
        } catch (err) {
          console.error("Failed to load user:", err);
        }
        setIsAuthenticating(false);
        return;
      }

      // Пытаемся авто-вход через Telegram
      try {
        const response = await apiClient.post(
          "/api/auth/telegram-login",
          {},
          {
            headers: { "X-Telegram-Init-Data": WebApp.initData },
          }
        );

        if (response.data.access_token) {
          setToken(response.data.access_token);
          if (response.data.user) {
            setUser(response.data.user);
          } else {
            await loadUser();
          }
        }
        setAuthError(null);
      } catch (err: any) {
        const status = err?.response?.status;
        const detail = err?.response?.data?.detail;

        if (status === 401) {
          if (detail?.includes("expired")) {
            setAuthError(
              "Сессия Telegram истекла. Пожалуйста, закройте и откройте приложение заново."
            );
          } else {
            setAuthError(
              "Не удалось подтвердить Telegram-аккаунт. Попробуйте открыть приложение заново."
            );
          }
        } else if (status === 409) {
          setAuthError(
            "Этот Telegram-аккаунт уже привязан к другому пользователю."
          );
        } else if (!err.response) {
          setAuthError(
            "Нет подключения к серверу. Проверьте интернет-соединение."
          );
        } else {
          setAuthError(detail || "Произошла ошибка при авторизации.");
        }
        console.error("Telegram auth failed:", err);
      } finally {
        setIsAuthenticating(false);
      }
    };

    initAuth();
  }, []); // Пустой массив — запускается только при монтировании

  return { isAuthenticating, authError };
};
