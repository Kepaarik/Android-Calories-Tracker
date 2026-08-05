import GlassButton from "../../components/ui/GlassButton";
import { ActivityLevel } from "../../types/api";
import "./ActivitySelector.css";

interface ActivityOption {
  value: ActivityLevel;
  label: string;
  description: string;
}

interface ActivitySelectorProps {
  levels: readonly ActivityOption[];
  selected: ActivityLevel;
  onChange: (level: ActivityLevel) => void;
}

export default function ActivitySelector({
  levels,
  selected,
  onChange,
}: ActivitySelectorProps) {
  return (
    <div className="activity-selector-list">
      {levels.map((level) => (
        <GlassButton
          type="button"
          key={level.value}
          onClick={() => onChange(level.value)}
          className="activity-selector-option"
          style={{
            background:
              selected === level.value
                ? "var(--glass-highlight)"
                : "var(--glass-bg)",
            border:
              selected === level.value
                ? "1px solid var(--glass-border)"
                : "1px solid var(--border-color)",
            fontWeight: selected === level.value ? "600" : "500",
          }}
        >
          <div className="activity-selector-option-label">
            {level.label}
          </div>
          <div className="activity-selector-option-description">
            {level.description}
          </div>
        </GlassButton>
      ))}
    </div>
  );
}
