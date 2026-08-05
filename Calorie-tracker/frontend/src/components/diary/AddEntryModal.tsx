// frontend/src/components/diary/AddEntryModal.tsx
import { useState, useEffect } from "react";
import Modal from "../ui/Modal";
import GlassButton from "../ui/GlassButton";
import ProductSearch from "./ProductSearch";
import { diaryApi } from "../../api/endpoints";
import { useToast } from "../../context/ToastContext";
import Icon from "../ui/Icon";
import { Product } from "../../types/api";
import { useSelectedDateStore } from "../../store/selectedDateStore";
import "./AddEntryModal.css";

type MealType = "breakfast" | "lunch" | "dinner" | "snack";

interface AddEntryModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAdded?: () => void;
}

export default function AddEntryModal({
  isOpen,
  onClose,
  onAdded,
}: AddEntryModalProps) {
  const toast = useToast();
  const { selectedDate } = useSelectedDateStore();
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [weight, setWeight] = useState("");
  const [mealType, setMealType] = useState<MealType>("breakfast");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const resetForm = () => {
    setSelectedProduct(null);
    setWeight("");
    setMealType("breakfast");
  };

  // Автоматический выбор приема пищи при открытии
  useEffect(() => {
    if (!isOpen) return;
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 11) setMealType("breakfast");
    else if (hour >= 11 && hour < 15) setMealType("lunch");
    else if (hour >= 15 && hour < 20) setMealType("dinner");
    else setMealType("snack");
  }, [isOpen]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProduct) {
      toast.warning("Выберите продукт");
      return;
    }
    const weightNum = parseFloat(weight);
    if (isNaN(weightNum) || weightNum <= 0) {
      toast.warning("Введите корректный вес");
      return;
    }

    setIsSubmitting(true);
    try {
      await diaryApi.addEntry({
        product_id: selectedProduct.id,
        weight_grams: weightNum,
        meal_type: mealType,
        date: selectedDate,
      });
      toast.success("Добавлено в дневник", selectedProduct.name);
      window.dispatchEvent(new CustomEvent("diaryUpdated"));
      resetForm();
      onAdded?.();
      onClose();
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка добавления");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleProductSelect = (product: Product) => {
    setSelectedProduct(product);
    // Убираем любой возможный toast здесь, если он был
  };

  const calcNutrient = (base: number) => {
    const w = parseFloat(weight) || 0;
    if (w <= 0) return 0;
    return Math.round((base * w) / 100 * 10) / 10;
  };

  const mealOptions: { value: MealType; label: string; emoji: string }[] = [
    { value: "breakfast", label: "Завтрак", emoji: "🌅" },
    { value: "lunch", label: "Обед", emoji: "☀️" },
    { value: "dinner", label: "Ужин", emoji: "🌙" },
    { value: "snack", label: "Перекус", emoji: "🍎" },
  ];

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Добавить запись">
      <form onSubmit={handleSubmit}>
        {/* Блок выбора продукта */}
        {!selectedProduct ? (
          <div className="add-entry-field">
            <label className="input-label">Продукт *</label>
            <ProductSearch onSelect={handleProductSelect} />
          </div>
        ) : (
          /* Карточка выбранного продукта */
          <div
            className="glass card add-entry-selected-product-card"
          >
            <div className="add-entry-product-icon">
              <Icon name="products" size={20} />
            </div>
            <div className="add-entry-product-info">
              <div className="add-entry-product-name">
                {selectedProduct.name}
              </div>
              <div className="add-entry-product-macros">
                <span className="add-entry-macro-calories">
                  {Math.round(selectedProduct.calories)} ккал
                </span>
                <span>•</span>
                <span className="add-entry-macro-protein">Б:{selectedProduct.proteins}</span>
                <span className="add-entry-macro-fat">Ж:{selectedProduct.fats}</span>
                <span className="add-entry-macro-carb">У:{selectedProduct.carbs}</span>
              </div>
            </div>
            <button
              type="button"
              onClick={() => setSelectedProduct(null)}
              title="Убрать продукт"
              className="add-entry-remove-product-btn"
            >
              <Icon name="close" size={16} />
            </button>
          </div>
        )}

        {/* Поле веса */}
        <div className="add-entry-field">
          <label className="input-label">Вес (граммы) *</label>
          <input
            type="number"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            placeholder="100"
            className="glass-input add-entry-weight-input"
            inputMode="numeric"
            min="1"
            max="10000"
            step="1"
          />
        </div>

        {/* Превью КБЖУ для текущего веса */}
        {selectedProduct && parseFloat(weight) > 0 && (
          <div className="add-entry-nutrient-preview-grid">
            {[
              { value: calcNutrient(selectedProduct.calories), label: "ккал", color: "var(--danger-color)" },
              { value: calcNutrient(selectedProduct.proteins), label: "Б", color: "var(--macro-protein-color)" },
              { value: calcNutrient(selectedProduct.fats), label: "Ж", color: "var(--warning-color)" },
              { value: calcNutrient(selectedProduct.carbs), label: "У", color: "var(--success-color)" },
            ].map((item) => (
              <div
                key={item.label}
                className="add-entry-nutrient-preview-item"
              >
                <div className="add-entry-nutrient-preview-value" style={{ color: item.color }}>
                  {item.value}
                </div>
                <div className="add-entry-nutrient-preview-label">
                  {item.label}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Выбор приема пищи */}
        <div className="add-entry-meal-type-section">
          <label className="input-label">Приём пищи</label>
          <div className="add-entry-meal-type-grid">
            {mealOptions.map((option) => {
              const isSelected = mealType === option.value;
              return (
                <GlassButton
                  key={option.value}
                  type="button"
                  variant={isSelected ? "success" : undefined}
                  onClick={() => setMealType(option.value)}
                  className="add-entry-meal-option-btn"
                  style={{ fontWeight: isSelected ? "600" : "400" }}
                >
                  <span>{option.emoji}</span>
                  <span>{option.label}</span>
                </GlassButton>
              );
            })}
          </div>
        </div>

        {/* Кнопки действия */}
        <div className="add-entry-actions">
          <GlassButton
            type="button"
            onClick={() => { resetForm(); onClose(); }}
            fullWidth
            className="add-entry-action-btn"
          >
            Отмена
          </GlassButton>
          <GlassButton
            type="submit"
            variant="success"
            fullWidth
            className="add-entry-action-btn"
            disabled={isSubmitting || !selectedProduct}
          >
            {isSubmitting ? "..." : "Добавить"}
          </GlassButton>
        </div>
      </form>
    </Modal>
  );
}