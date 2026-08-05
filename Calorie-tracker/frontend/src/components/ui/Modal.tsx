import { useEffect } from "react";
import { createPortal } from "react-dom";
import Icon from "./Icon";
import "./Modal.css";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  maxWidth?: string;
}

export default function Modal({
  isOpen,
  onClose,
  title,
  children,
  maxWidth = "500px",
}: ModalProps) {
  // Блокируем скролл body
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [isOpen]);

  // Закрытие по Escape
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) onClose();
    };
    window.addEventListener("keydown", handleEscape);
    return () => window.removeEventListener("keydown", handleEscape);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return createPortal(
    <div
      className="modal-overlay modal-overlay-backdrop"
      onClick={onClose}
    >
      <div
        className="glass card modal-content-box"
        onClick={(e) => e.stopPropagation()}
        style={{ maxWidth }}
      >
        {title && (
          <div className="modal-header">
            <h2 className="modal-title">
              {title}
            </h2>
            <button
              onClick={onClose}
              className="modal-close-button"
            >
              <Icon name="x" size={20} />
            </button>
          </div>
        )}

        <div className="modal-body">
          {children}
        </div>
      </div>
    </div>,
    document.body // ← Рендерим в body
  );
}
