import GlassButton from "../../components/ui/GlassButton";
import Icon from "../../components/ui/Icon";
import "./CalorieAdjuster.css";

interface CalorieAdjusterProps {
  value: number;
  onChange: (value: number) => void;
  step?: number;
}

export default function CalorieAdjuster({
  value,
  onChange,
  step = 50,
}: CalorieAdjusterProps) {
  const color =
    value === 0
      ? "var(--text-primary)"
      : value > 0
      ? "var(--success-color)"
      : "var(--danger-color)";

  return (
    <div>
      <div className="calorie-adjuster-controls">
        <GlassButton
          type="button"
          variant="icon"
          icon={<Icon name="minus" size={18} />}
          onClick={() => onChange(value - step)}
        />
        <div className="calorie-adjuster-value-wrapper">
          <div className="calorie-adjuster-value" style={{ color }}>
            {value > 0 ? "+" : ""}
            {value}
          </div>
          <div className="calorie-adjuster-unit">
            ккал
          </div>
        </div>
        <GlassButton
          type="button"
          variant="icon"
          icon={<Icon name="plus" size={18} />}
          onClick={() => onChange(value + step)}
        />
      </div>
      <p className="calorie-adjuster-hint">
        Используйте для тонкой настройки нормы
      </p>
    </div>
  );
}
