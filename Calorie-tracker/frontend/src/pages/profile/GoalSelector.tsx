import GlassButton from "../../components/ui/GlassButton";
import Icon from "../../components/ui/Icon";
import { FitnessGoal } from "../../types/api";
import "./GoalSelector.css";

interface GoalOption {
  value: FitnessGoal;
  label: string;
  description: string;
  icon: string;
}

interface GoalSelectorProps {
  goals: readonly GoalOption[];
  selected: FitnessGoal;
  onChange: (goal: FitnessGoal) => void;
}

export default function GoalSelector({
  goals,
  selected,
  onChange,
}: GoalSelectorProps) {
  return (
    <div className="goal-selector-list">
      {goals.map((goal) => (
        <GlassButton
          type="button"
          key={goal.value}
          onClick={() => onChange(goal.value)}
          className="goal-selector-option"
          style={{
            background:
              selected === goal.value
                ? "var(--glass-highlight)"
                : "var(--glass-bg)",
            border:
              selected === goal.value
                ? "1px solid var(--glass-border)"
                : "1px solid var(--border-color)",
            fontWeight: selected === goal.value ? "600" : "500",
          }}
        >
          <Icon name={goal.icon} size={20} />
          <div className="goal-selector-option-body">
            <div className="goal-selector-option-label">
              {goal.label}
            </div>
            <div className="goal-selector-option-description">
              {goal.description}
            </div>
          </div>
        </GlassButton>
      ))}
    </div>
  );
}
