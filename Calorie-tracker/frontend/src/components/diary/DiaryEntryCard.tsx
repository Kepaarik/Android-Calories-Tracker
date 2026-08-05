import { useState } from "react";
import { diaryApi } from "../../api/endpoints";
import { DiaryEntry } from "../../types/api";
import Icon from "../ui/Icon";
import GlassButton from "../ui/GlassButton";
import { useToast } from "../../context/ToastContext";
import "./DiaryEntryCard.css";

// Функция для получения ЛОКАЛЬНОЙ даты (как в Header)
const getLocalDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

interface DiaryEntryCardProps {
  entry: DiaryEntry;
  onUpdated: () => void;
  onDelete: (entry: DiaryEntry) => void;
}

export default function DiaryEntryCard({
  entry,
  onUpdated,
  onDelete,
}: DiaryEntryCardProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [weight, setWeight] = useState(entry.weight_grams.toString());
  const [time, setTime] = useState(() => {
    const dateStr = entry.consumed_at || entry.created_at;
    if (!dateStr) return "";
    try {
      const date = new Date(dateStr);
      const hours = date.getHours().toString().padStart(2, "0");
      const minutes = date.getMinutes().toString().padStart(2, "0");
      return `${hours}:${minutes}`;
    } catch {
      return "";
    }
  });
  const [isSaving, setIsSaving] = useState(false);
  const [isRepeating, setIsRepeating] = useState(false);
  const toast = useToast();

  const getOriginalTime = () => {
    const dateStr = entry.consumed_at || entry.created_at;
    if (!dateStr) return "";
    try {
      const date = new Date(dateStr);
      const hours = date.getHours().toString().padStart(2, "0");
      const minutes = date.getMinutes().toString().padStart(2, "0");
      return `${hours}:${minutes}`;
    } catch {
      return "";
    }
  };

  const handleSave = async () => {
    const weightNum = parseFloat(weight);
    if (isNaN(weightNum) || weightNum <= 0) {
      toast.warning("Введите корректный вес");
      return;
    }

    setIsSaving(true);
    try {
      const updateData: any = { weight_grams: weightNum };

      const originalTime = getOriginalTime();
      if (time && time !== originalTime) {
        const dateStr =
          entry.consumed_at || entry.created_at || new Date().toISOString();
        const date = new Date(dateStr);
        const [hours, minutes] = time.split(":").map(Number);
        date.setHours(hours, minutes, 0, 0);
        updateData.consumed_at = date.toISOString();
      }

      await diaryApi.updateEntry(entry.id, updateData);
      setIsEditing(false);
      onUpdated();
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка сохранения");
    } finally {
      setIsSaving(false);
    }
  };

  // НОВАЯ ФУНКЦИЯ: Повторить продукт в сегодняшнем дне
  const handleRepeat = async () => {
    if (!entry.product_id) {
      toast.warning("Продукт не найден");
      return;
    }

    setIsRepeating(true);
    try {
      await diaryApi.addEntry({
        product_id: entry.product_id,
        weight_grams: entry.weight_grams,
        meal_type: entry.meal_type,
        date: getLocalDate(),
      });
      toast.success(
        `Добавлено в сегодня: ${entry.product?.name || "Продукт"} (${
          entry.weight_grams
        }г)`
      );
      onUpdated(); // обновляем список дневника
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка добавления");
    } finally {
      setIsRepeating(false);
    }
  };

  const product = entry.product;
  const weightNum = parseFloat(weight) || 0;

  const calcNutrient = (base: number) => {
    const value = (base * weightNum) / 100;
    return isNaN(value) ? 0 : Math.round(value * 10) / 10;
  };

  const entryCalories = product ? calcNutrient(product.calories) : 0;
  const nutrients = product
    ? {
        proteins: calcNutrient(product.proteins),
        fats: calcNutrient(product.fats),
        carbs: calcNutrient(product.carbs),
      }
    : { proteins: 0, fats: 0, carbs: 0 };

  const formatTime = (dateString: string) => {
    if (!dateString) return "";
    try {
      const date = new Date(dateString);

      if (!dateString.includes("+") && !dateString.includes("Z")) {
        date.setHours(date.getHours() + 3);
      }

      return date.toLocaleTimeString("ru-RU", {
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return "";
    }
  };

  const displayTime = formatTime(entry.consumed_at || entry.created_at || "");

  return (
    <div
      className="glass card diary-entry-card"
    >
      {isEditing ? (
        <div>
          <div className="diary-entry-field">
            <label className="input-label">Вес (граммы)</label>
            <input
              type="number"
              value={weight}
              onChange={(e) => setWeight(e.target.value)}
              className="glass-input diary-entry-input"
              min="1"
              max="10000"
            />
          </div>

          <div className="diary-entry-field">
            <label className="input-label">Время приёма</label>
            <input
              type="time"
              value={time}
              onChange={(e) => setTime(e.target.value)}
              className="glass-input diary-entry-input"
            />
          </div>

          <div className="diary-entry-edit-actions">
            <GlassButton
              onClick={() => {
                setIsEditing(false);
                setWeight(entry.weight_grams.toString());
                setTime(getOriginalTime());
              }}
              className="diary-entry-edit-btn"
              disabled={isSaving}
            >
              Отмена
            </GlassButton>
            <GlassButton
              variant="success"
              onClick={handleSave}
              className="diary-entry-edit-btn"
              disabled={isSaving}
            >
              {isSaving ? "..." : "Сохранить"}
            </GlassButton>
          </div>
        </div>
      ) : (
        <div>
          {/* Заголовок с кнопками */}
          <div
            className="diary-entry-header"
          >
            <div
              className="diary-entry-title"
            >
              {product?.name || "Продукт"}
            </div>
            <div className="diary-entry-header-actions">
              {/* КНОПКА ПОВТОРИТЬ (НОВАЯ) */}
              <button
                onClick={handleRepeat}
                disabled={isRepeating}
                title="Добавить снова сегодня"
                className="diary-entry-icon-btn diary-entry-icon-btn-repeat"
              >
                {/* SVG иконка повтора (refresh) */}
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polyline points="23 4 23 10 17 10"></polyline>
                  <polyline points="1 20 1 14 7 14"></polyline>
                  <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                </svg>
              </button>

              {/* Кнопка редактировать */}
              <button
                onClick={() => setIsEditing(true)}
                className="diary-entry-icon-btn diary-entry-icon-btn-edit"
              >
                <Icon name="edit" size={16} />
              </button>

              {/* Кнопка удалить */}
              <button
                onClick={() => onDelete(entry)}
                className="diary-entry-icon-btn diary-entry-icon-btn-delete"
              >
                <Icon name="trash" size={16} />
              </button>
            </div>
          </div>

          {/* Вес и калории */}
          <div
            className="diary-entry-weight-calories"
          >
            {entry.weight_grams}г •{" "}
            <strong className="diary-entry-calories-value">
              {entryCalories} ккал
            </strong>
          </div>

          {/* БЖУ и время на одном уровне */}
          <div
            className="diary-entry-macros-row"
          >
            <div className="diary-entry-macros-text">
              Б: {nutrients.proteins}г &nbsp; Ж: {nutrients.fats}г &nbsp; У:{" "}
              {nutrients.carbs}г
            </div>
            {displayTime && (
              <div className="diary-entry-time">
                {displayTime}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
