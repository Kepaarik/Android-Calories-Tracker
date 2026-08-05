// frontend/src/components/ui/ThemeToggle.tsx
import { useThemeStore } from "../../store/themeStore";
import "./ThemeToggle.css";

export default function ThemeToggle() {
  const { theme, toggleTheme } = useThemeStore();

  return (
    <div className="theme-toggle-row">
      {/* Светлая тема */}
      <button
        onClick={() => theme !== "light" && toggleTheme()}
        className="theme-toggle-option theme-toggle-option-light"
        data-active={theme === "light"}
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill={theme === "light" ? "var(--primary-color)" : "none"}
          stroke={theme === "light" ? "var(--primary-color)" : "var(--text-secondary)"}
          strokeWidth="2"
        >
          <circle cx="12" cy="12" r="5"></circle>
          <line x1="12" y1="1" x2="12" y2="3"></line>
          <line x1="12" y1="21" x2="12" y2="23"></line>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
          <line x1="1" y1="12" x2="3" y2="12"></line>
          <line x1="21" y1="12" x2="23" y2="12"></line>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
        </svg>
        <span className="theme-toggle-label" data-active={theme === "light"}>
          Светлая
        </span>
      </button>

      {/* Тёмная тема */}
      <button
        onClick={() => theme !== "dark" && toggleTheme()}
        className="theme-toggle-option theme-toggle-option-dark"
        data-active={theme === "dark"}
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill={theme === "dark" ? "var(--primary-color)" : "none"}
          stroke={theme === "dark" ? "var(--primary-color)" : "var(--text-secondary)"}
          strokeWidth="2"
        >
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
        </svg>
        <span className="theme-toggle-label" data-active={theme === "dark"}>
          Тёмная
        </span>
      </button>
    </div>
  );
}