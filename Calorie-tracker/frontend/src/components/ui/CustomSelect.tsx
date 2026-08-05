import { useState } from "react";
import Modal from "./Modal";
import GlassButton from "./GlassButton";
import "./CustomSelect.css";

interface Option<T> {
  value: T;
  label: string;
  description?: string;
}

interface CustomSelectProps<T> {
  label: string;
  value: T;
  options: Option<T>[];
  onChange: (value: T) => void;
}

export default function CustomSelect<T extends string>({
  label,
  value,
  options,
  onChange,
}: CustomSelectProps<T>) {
  const [isOpen, setIsOpen] = useState(false);

  const selectedOption = options.find((o) => o.value === value);

  return (
    <>
      <div className="custom-select-wrapper">
        <label className="input-label">{label}</label>
        <button
          type="button"
          onClick={() => setIsOpen(true)}
          className="glass-input custom-select-trigger"
        >
          {selectedOption?.label || "Выберите..."}
        </button>
      </div>

      <Modal
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
        title={label}
        maxWidth="400px"
      >
        <div className="custom-select-options">
          {options.map((option) => {
            const isSelected = option.value === value;
            return (
              <GlassButton
                key={option.value}
                onClick={() => {
                  onChange(option.value);
                  setIsOpen(false);
                }}
                className="custom-select-option-btn"
                style={{
                  fontWeight: isSelected ? "600" : "400",
                  // ← ЯВНОЕ ВЫДЕЛЕНИЕ выбранного элемента через CSS переменные
                  ...(isSelected && {
                    background: "var(--active-bg)",
                    borderLeft: "3px solid var(--active-border)",
                    paddingLeft: "13px",
                    boxShadow: `inset 0 0 0 1px var(--active-border), 0 2px 8px var(--shadow-color)`,
                    color: "var(--active-text)",
                  }),
                }}
              >
                <span
                  className="custom-select-option-label"
                  style={{
                    color: isSelected ? "var(--active-text)" : undefined,
                  }}
                >
                  {option.label}
                </span>
                {isSelected && (
                  <svg
                    width="22"
                    height="22"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="var(--active-border)"
                    strokeWidth="2.5"
                  >
                    <polyline points="20 6 9 17 4 12"></polyline>
                  </svg>
                )}
              </GlassButton>
            );
          })}
        </div>

        <GlassButton
          onClick={() => setIsOpen(false)}
          fullWidth
          className="custom-select-cancel-btn"
        >
          Отмена
        </GlassButton>
      </Modal>
    </>
  );
}
