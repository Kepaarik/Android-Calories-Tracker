import {
  createContext,
  useContext,
  useState,
  useCallback,
  ReactNode,
} from "react";
import Toast from "../components/ui/Toast";
import "./ToastContext.css";

export type ToastType = "success" | "error" | "info" | "warning";

interface ToastMessage {
  id: string;
  message: string;
  subtitle?: string;
  type: ToastType;
  duration: number;
  onUndo?: () => void;
}

interface ToastContextType {
  success: (message: string, subtitle?: string) => void;
  error: (message: string, subtitle?: string) => void;
  info: (message: string, subtitle?: string) => void;
  warning: (message: string, subtitle?: string) => void;
  showWithUndo: (
    message: string,
    onUndo: () => void,
    subtitle?: string
  ) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const addToast = useCallback(
    (
      message: string,
      type: ToastType,
      subtitle?: string,
      duration = 3000,
      onUndo?: () => void
    ) => {
      const id = Math.random().toString(36).substr(2, 9);
      setToasts((prev) => [
        ...prev,
        { id, message, type, subtitle, duration, onUndo },
      ]);

      // Если нет кнопки Undo — убираем автоматически
      if (!onUndo) {
        setTimeout(() => removeToast(id), duration);
      }
    },
    [removeToast]
  );

  const success = useCallback(
    (message: string, subtitle?: string) => {
      addToast(message, "success", subtitle);
    },
    [addToast]
  );

  const error = useCallback(
    (message: string, subtitle?: string) => {
      addToast(message, "error", subtitle, 5000); // Ошибки показываем дольше
    },
    [addToast]
  );

  const info = useCallback(
    (message: string, subtitle?: string) => {
      addToast(message, "info", subtitle);
    },
    [addToast]
  );

  const warning = useCallback(
    (message: string, subtitle?: string) => {
      addToast(message, "warning", subtitle, 4000);
    },
    [addToast]
  );

  const showWithUndo = useCallback(
    (message: string, onUndo: () => void, subtitle?: string) => {
      addToast(message, "info", subtitle, 5000, onUndo);
    },
    [addToast]
  );

  return (
    <ToastContext.Provider
      value={{ success, error, info, warning, showWithUndo }}
    >
      {children}
      <div
        className="toast-host"
      >
        {toasts.map((toast) => (
          <div key={toast.id} className="toast-host-item">
            <Toast
              message={toast.message}
              subtitle={toast.subtitle}
              type={toast.type}
              onUndo={toast.onUndo}
              onClose={() => removeToast(toast.id)}
            />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return context;
}
