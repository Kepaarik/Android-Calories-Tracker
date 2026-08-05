import { useState, useEffect } from "react";
import { productsApi } from "../../api/endpoints";
import Modal from "../ui/Modal";
import GlassButton from "../ui/GlassButton";
import Icon from "../ui/Icon";
import { Product } from "../../types/api";
import { useConfirm } from "../../context/ConfirmContext";
import { useToast } from "../../context/ToastContext";
import "./EditProductModal.css";

interface Props {
  product: Product | null;
  isOpen: boolean;
  onClose: () => void;
  onUpdated: () => void;
}

// Формула пересчёта ккал из БЖУ
const calculateCalories = (
  proteins: number,
  fats: number,
  carbs: number
): number => {
  return Math.round((proteins * 4 + fats * 9 + carbs * 4) * 10) / 10;
};

export default function EditProductModal({
  product,
  isOpen,
  onClose,
  onUpdated,
}: Props) {
  const [name, setName] = useState("");
  const [calories, setCalories] = useState("");
  const [proteins, setProteins] = useState("");
  const [fats, setFats] = useState("");
  const [carbs, setCarbs] = useState("");
  const [autoCalc, setAutoCalc] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  const { confirm } = useConfirm();
  const toast = useToast();
  // При открытии модалки заполняем форму данными продукта
  useEffect(() => {
    if (product) {
      setName(product.name);
      setCalories(product.calories.toString());
      setProteins(product.proteins.toString());
      setFats(product.fats.toString());
      setCarbs(product.carbs.toString());
      setAutoCalc(false); // По умолчанию выключен
      setError("");
    }
  }, [product, isOpen]);

  // Авто-пересчёт ккал при изменении БЖУ
  useEffect(() => {
    if (!autoCalc) return;
    const p = parseFloat(proteins) || 0;
    const f = parseFloat(fats) || 0;
    const c = parseFloat(carbs) || 0;
    setCalories(calculateCalories(p, f, c).toString());
  }, [proteins, fats, carbs, autoCalc]);

  if (!product) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    if (!name.trim()) return setError("Введите название продукта");

    setIsSubmitting(true);
    try {
      await productsApi.update(product.id, {
        name: name.trim(),
        calories: parseFloat(calories) || 0,
        proteins: parseFloat(proteins) || 0,
        fats: parseFloat(fats) || 0,
        carbs: parseFloat(carbs) || 0,
      });
      onUpdated();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.detail || "Ошибка обновления");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    const ok = await confirm({
      title: "Удаление продукта",
      message: `Удалить продукт "${product.name}"? Это действие нельзя отменить.`,
      confirmText: "Удалить",
      variant: "danger",
    });
    if (!ok) return;
    try {
      await productsApi.delete(product.id);
      toast.success("Продукт удалён", product.name);
      onUpdated();
      onClose();
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка удаления");
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Редактировать продукт">
      <form onSubmit={handleSubmit}>
        {/* Название */}
        <div className="edit-product-field-group">
          <label className="input-label">Название *</label>
          <input
            type="text"
            className="glass-input edit-product-input"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        {/* Переключатель авто-пересчёта */}
        <div className="glass card edit-product-autocalc-card">
          <span className="edit-product-autocalc-label">
            Авто-пересчёт ккал
          </span>
          <button
            type="button"
            onClick={() => setAutoCalc(!autoCalc)}
            className="edit-product-autocalc-toggle"
            data-active={autoCalc}
          >
            <span
              className="edit-product-autocalc-knob"
              style={{ left: autoCalc ? "20px" : "2px" }}
            />
          </button>
        </div>

        {/* БЖУ и ккал */}
        <div className="edit-product-macros-grid">
          <div>
            <label className="input-label">Калории (ккал)</label>
            <input
              type="number"
              className="glass-input edit-product-input edit-product-calories-input"
              value={calories}
              onChange={(e) => setCalories(e.target.value)}
              placeholder="0"
              step="0.1"
              readOnly={autoCalc}
              data-readonly={autoCalc}
            />
          </div>
          <div>
            <label className="input-label">Белки (г)</label>
            <input
              type="number"
              className="glass-input edit-product-input"
              value={proteins}
              onChange={(e) => setProteins(e.target.value)}
              placeholder="0"
              step="0.1"
            />
          </div>
          <div>
            <label className="input-label">Жиры (г)</label>
            <input
              type="number"
              className="glass-input edit-product-input"
              value={fats}
              onChange={(e) => setFats(e.target.value)}
              placeholder="0"
              step="0.1"
            />
          </div>
          <div>
            <label className="input-label">Углеводы (г)</label>
            <input
              type="number"
              className="glass-input edit-product-input"
              value={carbs}
              onChange={(e) => setCarbs(e.target.value)}
              placeholder="0"
              step="0.1"
            />
          </div>
        </div>

        {error && (
          <div className="edit-product-error-banner">
            {error}
          </div>
        )}

        {/* Кнопки */}

        <div className="edit-product-buttons-row">
          <GlassButton
            type="button"
            onClick={onClose}
            fullWidth
            className="edit-product-button"
          >
            Отмена
          </GlassButton>
          <GlassButton
            type="submit"
            variant="success"
            fullWidth
            className="edit-product-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? "..." : "Сохранить"}
          </GlassButton>
        </div>
      </form>
    </Modal>
  );
}
