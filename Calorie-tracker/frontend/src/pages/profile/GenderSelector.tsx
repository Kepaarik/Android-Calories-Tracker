import GlassButton from "../../components/ui/GlassButton";
import Icon from "../../components/ui/Icon";
import { Gender } from "../../types/api";
import "./GenderSelector.css";

interface GenderSelectorProps {
  selected: Gender;
  onChange: (gender: Gender) => void;
}

export default function GenderSelector({
  selected,
  onChange,
}: GenderSelectorProps) {
  return (
    <div className="gender-selector-wrapper">
      <GlassButton
        type="button"
        onClick={() => onChange("male")}
        className="gender-selector-option"
        style={{
          background:
            selected === "male" ? "var(--glass-highlight)" : "var(--glass-bg)",
          border:
            selected === "male"
              ? "1px solid var(--glass-border)"
              : "1px solid var(--border-color)",
          fontWeight: selected === "male" ? "600" : "500",
        }}
      >
        <Icon name="male" size={18} />
        Мужской
      </GlassButton>
      <GlassButton
        type="button"
        onClick={() => onChange("female")}
        className="gender-selector-option"
        style={{
          background:
            selected === "female"
              ? "var(--glass-highlight)"
              : "var(--glass-bg)",
          border:
            selected === "female"
              ? "1px solid var(--glass-border)"
              : "1px solid var(--border-color)",
          fontWeight: selected === "female" ? "600" : "500",
        }}
      >
        <Icon name="female" size={18} />
        Женский
      </GlassButton>
    </div>
  );
}
