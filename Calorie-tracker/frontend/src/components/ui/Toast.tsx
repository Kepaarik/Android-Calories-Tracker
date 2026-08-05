// frontend/src/components/ui/Toast.tsx
import { useEffect } from "react";
import { createPortal } from "react-dom";
import Icon from "./Icon";
import { ToastType } from "../../context/ToastContext";
import "./Toast.css";

interface ToastProps {
  message: string;
  subtitle?: string;
  type?: ToastType; // ← Добавили тип из контекста
  onUndo?: () => void;
  onClose: () => void;
  duration?: number;
}

// Настройки иконок и цветов для разных типов уведомлений
const TYPE_CONFIG: Record<ToastType, { icon: string; color: string }> = {
  success: { icon: "check-circle", color: "var(--success-color)" }, // Зеленый
  error: { icon: "alert-circle", color: "var(--danger-color)" }, // Красный
  warning: { icon: "alert-triangle", color: "var(--warning-color)" }, // Оранжевый
  info: { icon: "info", color: "var(--macro-protein-color)" }, // Синий
};

export default function Toast({
  message,
  subtitle,
  type = "info", // ← По умолчанию info
  onUndo,
  onClose,
  duration = 5000,
}: ToastProps) {
  useEffect(() => {
    const timeoutId = setTimeout(onClose, duration);
    return () => clearTimeout(timeoutId);
  }, [onClose, duration]);

  const config = TYPE_CONFIG[type] || TYPE_CONFIG.info;

  return createPortal(
    <div className="undo-toast">
      <div className="undo-toast-content">
        <div className="toast-row">
          {/* 1. Иконка статуса (слева) */}
          <div className="toast-icon" style={{ color: config.color }}>
            <Icon name={config.icon} size={22} />
          </div>

          {/* 2. Текст (по центру) */}
          <div className="toast-text">
            <div
              className={`toast-message${subtitle ? " toast-message-with-subtitle" : ""}`}
            >
              {message}
            </div>
            {subtitle && (
              <div className="toast-subtitle">
                {subtitle}
              </div>
            )}
          </div>

          {/* 3. Кнопки (справа) */}
          <div className="toast-actions">
            {onUndo && (
              <button
                onClick={() => {
                  onUndo();
                  onClose(); // Закрываем тост после отмены
                }}
                className="undo-toast-btn"
              >
                Отменить
              </button>
            )}
            <button onClick={onClose} className="undo-toast-close">
              <Icon name="close" size={16} />
            </button>
          </div>
        </div>

        {/* 4. Прогресс-бар (снизу) */}
        <div
          className="undo-toast-progress"
          style={{
            animationDuration: `${duration}ms`,
            background: config.color, // ← Прогресс-бар окрашивается в цвет типа!
          }}
        />
      </div>
    </div>,
    document.body
  );
}
