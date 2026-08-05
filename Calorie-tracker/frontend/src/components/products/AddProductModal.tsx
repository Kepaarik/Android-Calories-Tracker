import { useState } from "react";
import { productsApi } from "../../api/endpoints";
import Modal from "../ui/Modal";
import GlassButton from "../ui/GlassButton";
import BarcodeScanner from "./BarcodeScanner";
import {
  getProductByBarcode,
  convertToProductData,
} from "../../api/openFoodFacts";
import { useToast } from "../../context/ToastContext";
import "./AddProductModal.css";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onAdded: () => void;
}

export default function AddProductModal({ isOpen, onClose, onAdded }: Props) {
  const toast = useToast();
  const [name, setName] = useState("");
  const [calories, setCalories] = useState("");
  const [proteins, setProteins] = useState("");
  const [fats, setFats] = useState("");
  const [carbs, setCarbs] = useState("");
  const [barcode, setBarcode] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState("");

  const resetForm = () => {
    setName("");
    setCalories("");
    setProteins("");
    setFats("");
    setCarbs("");
    setBarcode("");
    setError("");
  };

  const handleBarcodeScan = async (scannedBarcode: string) => {
    setIsSearching(true);
    setError("");
    setBarcode(scannedBarcode);

    try {
      // 1. Проверяем локальную БД
      try {
        const existingRes = await productsApi.getAll({ search: scannedBarcode });
        const existing = existingRes.data.find((p) => p.barcode === scannedBarcode);
        if (existing) {
          toast.info("Продукт уже есть в базе", existing.name);
          setName(existing.name);
          setCalories(existing.calories.toString());
          setProteins(existing.proteins.toString());
          setFats(existing.fats.toString());
          setCarbs(existing.carbs.toString());
          return;
        }
      } catch (err) {
        console.warn("Failed to check duplicates:", err);
      }

      // 2. Ищем в Open Food Facts
      const product = await getProductByBarcode(scannedBarcode);
      if (product) {
        const converted = convertToProductData(product, scannedBarcode);
        setName(converted.name);
        setCalories(converted.calories.toString());
        setProteins(converted.proteins.toString());
        setFats(converted.fats.toString());
        setCarbs(converted.carbs.toString());
        toast.success("Продукт найден", converted.name);
      } else {
        setError(`Продукт со штрихкодом ${scannedBarcode} не найден. Заполните данные вручную.`);
      }
    } catch (err) {
      console.error("Scan error:", err);
      setError("Ошибка при получении данных о продукте");
    } finally {
      setIsSearching(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!name.trim()) {
      toast.warning("Введите название продукта");
      return;
    }

    setIsSubmitting(true);
    try {
      await productsApi.create({
        name: name.trim(),
        calories: parseFloat(calories) || 0,
        proteins: parseFloat(proteins) || 0,
        fats: parseFloat(fats) || 0,
        carbs: parseFloat(carbs) || 0,
        barcode: barcode.trim() || null,
      });

      toast.success("Продукт добавлен", name.trim());

      // ← КЛЮЧЕВОЕ: сначала обновляем список, потом закрываем
      await onAdded();

      resetForm();
      onClose();
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка добавления");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Новый продукт">
      <form onSubmit={handleSubmit}>
        {/* Штрихкод */}
        <div className="add-product-field-group">
          <label className="input-label">Штрихкод (необязательно)</label>
          <BarcodeScanner onScanned={handleBarcodeScan} />
        </div>

        {/* Индикатор поиска */}
        {isSearching && (
          <div className="add-product-search-indicator">
            Поиск продукта по штрихкоду...
          </div>
        )}

        {/* Название */}
        <div className="add-product-field-group">
          <label className="input-label">Название *</label>
          <input
            type="text"
            className="glass-input add-product-input"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Например: Куриная грудка"
          />
        </div>

        {/* КБЖУ */}
        <div className="add-product-macros-grid">
          {[
            { label: "Калории (ккал)", value: calories, setter: setCalories },
            { label: "Белки (г)", value: proteins, setter: setProteins },
            { label: "Жиры (г)", value: fats, setter: setFats },
            { label: "Углеводы (г)", value: carbs, setter: setCarbs },
          ].map((field) => (
            <div key={field.label}>
              <label className="input-label">{field.label}</label>
              <input
                type="number"
                className="glass-input add-product-input"
                value={field.value}
                onChange={(e) => field.setter(e.target.value)}
                placeholder="0"
                step="0.1"
              />
            </div>
          ))}
        </div>

        {/* Ошибка */}
        {error && (
          <div className="add-product-error-banner">
            {error}
          </div>
        )}

        {/* Кнопки */}
        <div className="add-product-buttons-row">
          <GlassButton
            type="button"
            onClick={onClose}
            fullWidth
            className="add-product-button"
          >
            Отмена
          </GlassButton>
          <GlassButton
            type="submit"
            variant="success"
            fullWidth
            className="add-product-button"
            disabled={isSubmitting || isSearching}
          >
            {isSubmitting ? "..." : "Создать"}
          </GlassButton>
        </div>
      </form>
    </Modal>
  );
}