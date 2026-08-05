import { useState, useEffect, useRef } from "react";
import { productsApi } from "../../api/endpoints";
import { Product } from "../../types/api";
import Modal from "../ui/Modal";
import GlassButton from "../ui/GlassButton";
import Icon from "../ui/Icon";
import DropdownPortal from "../ui/DropdownPortal";
import "./BuildDishModal.css";
import { useToast } from "../../context/ToastContext";

interface BuildDishModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreated: () => void;
}

interface Ingredient {
  product: Product;
  weight: string;
}

export default function BuildDishModal({
  isOpen,
  onClose,
  onCreated,
}: BuildDishModalProps) {
  const [dishName, setDishName] = useState("");
  const [totalWeight, setTotalWeight] = useState("");
  const [search, setSearch] = useState("");
  const [searchResults, setSearchResults] = useState<Product[]>([]);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [isSaving, setIsSaving] = useState(false);
  const searchInputRef = useRef<HTMLInputElement>(null);
  const toast = useToast();

  useEffect(() => {
    if (search.length < 2) {
      setSearchResults([]);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const res = await productsApi.getAll({ search });
        const addedIds = new Set(ingredients.map((i) => i.product.id));
        setSearchResults(res.data.filter((p: Product) => !addedIds.has(p.id)));
      } catch (err) {
        console.error("Ошибка поиска:", err);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [search, ingredients]);

  const addIngredient = (product: Product) => {
    setIngredients([...ingredients, { product, weight: "100" }]);
    setSearch("");
    setSearchResults([]);
  };

  const updateWeight = (index: number, weight: string) => {
    const updated = [...ingredients];
    updated[index].weight = weight;
    setIngredients(updated);
  };

  const removeIngredient = (index: number) => {
    setIngredients(ingredients.filter((_, i) => i !== index));
  };

  const ingredientsWeight = ingredients.reduce(
    (sum, i) => sum + (parseFloat(i.weight) || 0),
    0
  );

  const totals = ingredients.reduce(
    (acc, i) => {
      const w = (parseFloat(i.weight) || 0) / 100;
      return {
        calories: acc.calories + i.product.calories * w,
        proteins: acc.proteins + i.product.proteins * w,
        fats: acc.fats + i.product.fats * w,
        carbs: acc.carbs + i.product.carbs * w,
      };
    },
    { calories: 0, proteins: 0, fats: 0, carbs: 0 }
  );

  const finalWeight = parseFloat(totalWeight) || ingredientsWeight;

  const per100g =
    finalWeight > 0
      ? {
          calories: Math.round((totals.calories / finalWeight) * 100),
          proteins: Math.round((totals.proteins / finalWeight) * 100 * 10) / 10,
          fats: Math.round((totals.fats / finalWeight) * 100 * 10) / 10,
          carbs: Math.round((totals.carbs / finalWeight) * 100 * 10) / 10,
        }
      : { calories: 0, proteins: 0, fats: 0, carbs: 0 };

  const handleSave = async () => {
    if (!dishName.trim()) {
      toast.warning("Введите название блюда");
      return;
    }
    if (ingredients.length === 0) {
      toast.warning("Добавьте хотя бы один ингредиент");
      return;
    }
    if (finalWeight <= 0) {
      toast.warning("Укажите вес готового блюда");
      return;
    }

    setIsSaving(true);
    try {
      await productsApi.createComposite({
        name: dishName.trim(),
        total_weight: finalWeight,
        ingredients: ingredients.map((i) => ({
          ingredient_id: i.product.id,
          weight_grams: parseFloat(i.weight) || 0,
        })),
      });
      toast.success("Блюдо сохранено", dishName);
      onCreated();
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка сохранения");
    } finally {
      setIsSaving(false);
    }
  };

  const handleClose = () => {
    setDishName("");
    setTotalWeight("");
    setIngredients([]);
    setSearch("");
    setSearchResults([]);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Собрать блюдо">
      {/* Название блюда */}
      <div className="build-dish-field-group">
        <label className="input-label">Название блюда</label>
        <input
          type="text"
          value={dishName}
          onChange={(e) => setDishName(e.target.value)}
          className="glass-input build-dish-input"
          placeholder="Например: Овсянка с бананом"
        />
      </div>

      {/* Поиск ингредиентов */}
      <div className="build-dish-search-wrapper">
        <label className="input-label">Добавить ингредиент</label>
        <input
          ref={searchInputRef}
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="glass-input build-dish-input"
          placeholder="Поиск продукта..."
        />
        <DropdownPortal
          isOpen={searchResults.length > 0}
          anchorRef={searchInputRef}
        >
          {searchResults.map((product) => (
            <div
              key={product.id}
              onClick={() => addIngredient(product)}
              className="build-dish-search-result-item"
              onMouseEnter={(e) =>
                (e.currentTarget.style.background = "var(--glass-focus)")
              }
              onMouseLeave={(e) =>
                (e.currentTarget.style.background = "transparent")
              }
            >
              <div className="build-dish-search-result-name">
                {product.name}
              </div>
              <div className="build-dish-search-result-meta">
                {Math.round(product.calories)} ккал • Б:{product.proteins} Ж:
                {product.fats} У:{product.carbs}
              </div>
            </div>
          ))}
        </DropdownPortal>
      </div>

      {/* Список ингредиентов */}
      {ingredients.length > 0 && (
        <div className="build-dish-field-group">
          <div className="build-dish-count-summary">
            Ингредиенты ({ingredients.length}) • Суммарный вес:{" "}
            {Math.round(ingredientsWeight)}г
          </div>
          <div className="build-dish-ingredients">
            {ingredients.map((item, index) => {
              const w = (parseFloat(item.weight) || 0) / 100;
              const ingCalories = Math.round(item.product.calories * w);
              const ingProteins =
                Math.round(item.product.proteins * w * 10) / 10;
              const ingFats = Math.round(item.product.fats * w * 10) / 10;
              const ingCarbs = Math.round(item.product.carbs * w * 10) / 10;

              return (
                <div
                  key={item.product.id}
                  className="product-card build-dish-card"
                >
                  <div className="product-content">
                    <div className="product-name">{item.product.name}</div>
                    <div className="product-nutrients">
                      <span className="nutrient calories">
                        <span className="nutrient-value">{ingCalories}</span>
                        <span className="nutrient-label">ккал</span>
                      </span>
                      <span className="nutrient">
                        <span className="nutrient-value">
                          {ingProteins.toFixed(1)}
                        </span>
                        <span className="nutrient-label">Б</span>
                      </span>
                      <span className="nutrient">
                        <span className="nutrient-value">
                          {ingFats.toFixed(1)}
                        </span>
                        <span className="nutrient-label">Ж</span>
                      </span>
                      <span className="nutrient">
                        <span className="nutrient-value">
                          {ingCarbs.toFixed(1)}
                        </span>
                        <span className="nutrient-label">У</span>
                      </span>
                    </div>
                  </div>
                  <div className="build-dish-actions">
                    <div className="build-dish-weight">
                      <input
                        type="number"
                        value={item.weight}
                        onChange={(e) => updateWeight(index, e.target.value)}
                        className="glass-input"
                        min="1"
                      />
                      <span className="weight-unit">г</span>
                    </div>
                    <button
                      onClick={() => removeIngredient(index)}
                      className="action-btn delete"
                      title="Удалить"
                    >
                      <Icon name="trash" size={16} />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Вес готового блюда */}
      {ingredients.length > 0 && (
        <div className="build-dish-field-group">
          <label className="input-label">Вес готового блюда (граммы)</label>
          <input
            type="number"
            value={totalWeight}
            onChange={(e) => setTotalWeight(e.target.value)}
            className="glass-input build-dish-input"
            placeholder={`По умолчанию: ${Math.round(ingredientsWeight)}г`}
            min="1"
          />
        </div>
      )}

      {/* Итоги */}
      {ingredients.length > 0 && (
        <div className="build-dish-totals-section">
          {/* На 100г */}
          <label className="input-label build-dish-total-label-block">
            На 100г
          </label>
          <div className="product-card build-dish-total build-dish-total-spacer">
            <div className="product-content build-dish-total-content">
              <div className="build-dish-total-nutrients">
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value calories-value">
                    {per100g.calories}
                  </span>
                  <span className="build-dish-total-label">ккал</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value proteins-value">
                    {per100g.proteins.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">Б</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value fats-value">
                    {per100g.fats.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">Ж</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value carbs-value">
                    {per100g.carbs.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">У</span>
                </span>
              </div>
            </div>
          </div>

          {/* Всего в блюде */}
          <label className="input-label build-dish-total-label-block">
            Всего в блюде ({Math.round(finalWeight)}г)
          </label>
          <div className="product-card build-dish-total">
            <div className="product-content build-dish-total-content">
              <div className="build-dish-total-nutrients">
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value calories-value">
                    {Math.round(totals.calories)}
                  </span>
                  <span className="build-dish-total-label">ккал</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value proteins-value">
                    {totals.proteins.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">Б</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value fats-value">
                    {totals.fats.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">Ж</span>
                </span>
                <span className="build-dish-total-nutrient">
                  <span className="build-dish-total-value carbs-value">
                    {totals.carbs.toFixed(1)}
                  </span>
                  <span className="build-dish-total-label">У</span>
                </span>
              </div>
            </div>
          </div>
        </div>
      )}
      {/* Кнопки */}
      <div className="build-dish-buttons-row">
        <GlassButton
          onClick={handleClose}
          fullWidth
          className="build-dish-button"
          disabled={isSaving}
        >
          Отмена
        </GlassButton>
        <GlassButton
          variant="success"
          onClick={handleSave}
          fullWidth
          className="build-dish-button"
          disabled={isSaving || ingredients.length === 0}
        >
          {isSaving ? "Сохранение..." : "Сохранить блюдо"}
        </GlassButton>
      </div>
    </Modal>
  );
}
