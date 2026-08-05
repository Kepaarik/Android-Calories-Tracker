import { useState } from "react";
import GlassButton from "../ui/GlassButton";
import "./BarcodeScanner.css";

interface BarcodeScannerProps {
  onScanned: (barcode: string) => void;
}

export default function BarcodeScanner({ onScanned }: BarcodeScannerProps) {
  const [value, setValue] = useState("");

  const handleSearch = () => {
    const trimmed = value.trim();
    if (!trimmed) return;
    onScanned(trimmed);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault(); // Предотвращаем сабмит родительской формы
      handleSearch();
    }
  };

  return (
    <div className="barcode-scanner-row">
      <input
        type="text"
        inputMode="numeric"
        placeholder="Введите штрих-код продукта"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        className="glass-input barcode-scanner-input"
      />
      <GlassButton
        type="button" // ← НЕ submit! Обычная кнопка
        variant="success"
        onClick={handleSearch}
        className="barcode-scanner-button"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        Найти
      </GlassButton>
    </div>
  );
}