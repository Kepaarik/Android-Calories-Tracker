import {
  createContext,
  useContext,
  useState,
  useCallback,
  ReactNode,
} from "react";
import Modal from "../components/ui/Modal";
import GlassButton from "../components/ui/GlassButton";
import "./ConfirmContext.css";

interface ConfirmOptions {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: "danger" | "primary";
}

interface ConfirmContextType {
  confirm: (options: ConfirmOptions) => Promise<boolean>;
}

const ConfirmContext = createContext<ConfirmContextType | undefined>(undefined);

export function ConfirmProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<{
    isOpen: boolean;
    options: ConfirmOptions;
    resolve?: (value: boolean) => void;
  }>({
    isOpen: false,
    options: { message: "" },
  });

  const confirm = useCallback((options: ConfirmOptions): Promise<boolean> => {
    return new Promise((resolve) => {
      setState({ isOpen: true, options, resolve });
    });
  }, []);

  const handleConfirm = () => {
    state.resolve?.(true);
    setState({ isOpen: false, options: { message: "" } });
  };

  const handleCancel = () => {
    state.resolve?.(false);
    setState({ isOpen: false, options: { message: "" } });
  };

  return (
    <ConfirmContext.Provider value={{ confirm }}>
      {children}
      <Modal
        isOpen={state.isOpen}
        onClose={handleCancel}
        title={state.options.title || "Подтверждение"}
      >
        <p
          className="confirm-dialog-message"
        >
          {state.options.message}
        </p>
        <div className="confirm-dialog-actions">
          <GlassButton
            onClick={handleCancel}
            fullWidth
            className="confirm-dialog-btn"
          >
            {state.options.cancelText || "Отмена"}
          </GlassButton>
          <GlassButton
            variant={state.options.variant === "danger" ? "danger" : "success"}
            onClick={handleConfirm}
            fullWidth
            className="confirm-dialog-btn"
          >
            {state.options.confirmText || "Подтвердить"}
          </GlassButton>
        </div>
      </Modal>
    </ConfirmContext.Provider>
  );
}

export function useConfirm() {
  const context = useContext(ConfirmContext);
  if (!context) {
    throw new Error("useConfirm must be used within a ConfirmProvider");
  }
  return context;
}
