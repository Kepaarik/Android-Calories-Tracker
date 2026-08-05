// frontend/src/components/telegram/TelegramThemeSync.tsx
import { useEffect } from "react";

export default function TelegramThemeSync() {
  useEffect(() => {
    // Проверяем сохранённую тему
    const savedTheme = localStorage.getItem("theme");

    if (savedTheme) {
      document.documentElement.setAttribute("data-theme", savedTheme);
    } else {
      // Если нет сохранённой темы, используем системную
      const prefersDark = window.matchMedia(
        "(prefers-color-scheme: dark)"
      ).matches;
      document.documentElement.setAttribute(
        "data-theme",
        prefersDark ? "dark" : "light"
      );
    }

    // Слушаем изменения системной темы
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    const handleChange = (e: MediaQueryListEvent) => {
      if (!localStorage.getItem("theme")) {
        document.documentElement.setAttribute(
          "data-theme",
          e.matches ? "dark" : "light"
        );
      }
    };

    mediaQuery.addEventListener("change", handleChange);
    return () => mediaQuery.removeEventListener("change", handleChange);
  }, []);

  return null;
}
