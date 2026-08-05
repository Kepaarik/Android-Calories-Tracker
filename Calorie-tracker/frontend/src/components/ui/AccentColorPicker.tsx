// frontend/src/components/ui/AccentColorPicker.tsx
import {
  useThemeStore,
  DEFAULT_ACCENT_LIGHT,
  DEFAULT_ACCENT_DARK,
} from "../../store/themeStore";
import "./AccentColorPicker.css";

const PRESET_COLORS = [
  "#0077b3",
  "#00a8cc",
  "#7c4dff",
  "#2e7d32",
  "#e65100",
  "#d81b60",
  "#c62828",
];

export default function AccentColorPicker() {
  const { theme, accentColor, setAccentColor } = useThemeStore();
  const defaultAccentColor =
    theme === "light" ? DEFAULT_ACCENT_LIGHT : DEFAULT_ACCENT_DARK;
  const activeColor = accentColor || defaultAccentColor;

  return (
    <div className="accent-picker">
      <div className="accent-picker-swatches">
        {PRESET_COLORS.map((color) => (
          <button
            key={color}
            type="button"
            className="accent-picker-swatch"
            style={{ background: color }}
            data-active={activeColor.toLowerCase() === color.toLowerCase()}
            title={color}
            onClick={() => setAccentColor(color)}
          />
        ))}

        <label className="accent-picker-swatch accent-picker-custom" title="Свой цвет">
          <input
            type="color"
            value={activeColor}
            onChange={(e) => setAccentColor(e.target.value)}
            className="accent-picker-custom-input"
          />
        </label>
      </div>

      {accentColor && (
        <button
          type="button"
          className="accent-picker-reset"
          onClick={() => setAccentColor(null)}
        >
          Сбросить по умолчанию
        </button>
      )}
    </div>
  );
}
