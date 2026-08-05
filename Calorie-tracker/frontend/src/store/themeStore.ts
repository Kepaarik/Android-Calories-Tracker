// frontend/src/store/themeStore.ts
import { create } from "zustand";
import { themeSettingsApi } from "../api/endpoints";

type Theme = "light" | "dark";

export const DEFAULT_ACCENT_LIGHT = "#0077b3";
export const DEFAULT_ACCENT_DARK = "#00a8cc";

const hexToRgb = (hex: string) => {
  const clean = hex.replace("#", "");
  const r = parseInt(clean.substring(0, 2), 16);
  const g = parseInt(clean.substring(2, 4), 16);
  const b = parseInt(clean.substring(4, 6), 16);
  return { r, g, b };
};

const applyAccentColor = (theme: Theme, accent: string | null) => {
  const root = document.documentElement.style;

  if (!accent) {
    root.removeProperty("--primary-color");
    root.removeProperty("--primary-light");
    root.removeProperty("--glass-focus");
    root.removeProperty("--active-bg");
    root.removeProperty("--active-border");
    root.removeProperty("--active-text");
    return;
  }

  const { r, g, b } = hexToRgb(accent);
  root.setProperty("--primary-color", accent);
  root.setProperty("--primary-light", `rgba(${r}, ${g}, ${b}, 0.2)`);
  root.setProperty("--glass-focus", `rgba(${r}, ${g}, ${b}, 0.25)`);
  root.setProperty("--active-bg", `rgba(${r}, ${g}, ${b}, ${theme === "light" ? 0.18 : 0.25})`);
  root.setProperty("--active-border", `rgba(${r}, ${g}, ${b}, ${theme === "light" ? 0.6 : 0.7})`);
  root.setProperty("--active-text", accent);
};

const applyTheme = (theme: Theme, accentColor: string | null) => {
  document.documentElement.setAttribute("data-theme", theme);
  applyAccentColor(theme, accentColor);
};

const getInitialTheme = (): Theme => {
  const saved = localStorage.getItem("theme");
  if (saved === "dark" || saved === "light") return saved;
  return window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light";
};

const initialTheme = getInitialTheme();
const initialAccentColor = localStorage.getItem("accentColor");
applyTheme(initialTheme, initialAccentColor);

// Если пользователь успел вручную сменить тему/цвет до того, как пришёл ответ
// syncFromProfile, его выбор не должен быть перезаписан устаревшими данными с сервера.
let modifiedSinceBoot = false;

interface ThemeState {
  theme: Theme;
  accentColor: string | null;
  toggleTheme: () => void;
  setAccentColor: (color: string | null) => void;
  syncFromProfile: () => Promise<void>;
}

export const useThemeStore = create<ThemeState>((set, get) => ({
  theme: initialTheme,
  accentColor: initialAccentColor,

  toggleTheme: () => {
    modifiedSinceBoot = true;
    const nextTheme: Theme = get().theme === "light" ? "dark" : "light";
    localStorage.setItem("theme", nextTheme);
    applyTheme(nextTheme, get().accentColor);
    set({ theme: nextTheme });
    themeSettingsApi
      .saveSettings({ theme: nextTheme, accent_color: get().accentColor })
      .catch(() => {});
  },

  setAccentColor: (color) => {
    modifiedSinceBoot = true;
    if (color) localStorage.setItem("accentColor", color);
    else localStorage.removeItem("accentColor");
    applyTheme(get().theme, color);
    set({ accentColor: color });
    themeSettingsApi
      .saveSettings({ theme: get().theme, accent_color: color })
      .catch(() => {});
  },

  syncFromProfile: async () => {
    try {
      const res = await themeSettingsApi.getSettings();
      if (modifiedSinceBoot) return; // пользователь уже сделал свой выбор — не затираем его

      const { theme, accent_color } = res.data;
      // Сервер ничего не сохранял (нет профиля или ещё не сохраняли тему) — оставляем локальные настройки как есть
      if (theme !== "light" && theme !== "dark" && !accent_color) return;

      const nextTheme: Theme =
        theme === "light" || theme === "dark" ? theme : get().theme;
      const nextAccent = accent_color ?? get().accentColor;

      localStorage.setItem("theme", nextTheme);
      if (nextAccent) localStorage.setItem("accentColor", nextAccent);

      applyTheme(nextTheme, nextAccent);
      set({ theme: nextTheme, accentColor: nextAccent });
    } catch {
      // Профиль ещё не создан или недоступен — остаёмся на локальных настройках
    }
  },
}));
