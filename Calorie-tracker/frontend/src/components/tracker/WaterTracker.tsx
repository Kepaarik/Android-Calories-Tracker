// frontend/src/components/tracker/WaterTracker.tsx
// frontend/src/components/tracker/WaterTracker.tsx
import { useState, useEffect } from "react";
import "./WaterTracker.css";

interface WaterTrackerProps {
  date?: string; // ← Добавляем проп
}

export default function WaterTracker({ date }: WaterTrackerProps) {
  const [glasses, setGlasses] = useState(0);
  const TARGET_GLASSES = 8;

  // Используем переданную дату или сегодня
  const currentDate = date || new Date().toISOString().split("T")[0];

  useEffect(() => {
    // ← ИСПОЛЬЗУЕМ ДАТУ В КЛЮЧЕ
    const storageKey = `water_${currentDate}`;
    const saved = localStorage.getItem(storageKey);

    if (saved) {
      setGlasses(parseInt(saved));
    } else {
      setGlasses(0);
    }
  }, [currentDate]); // ← Зависимость от даты

  const saveToStorage = (count: number) => {
    const storageKey = `water_${currentDate}`;
    localStorage.setItem(storageKey, count.toString());
  };

  const addGlass = () => {
    if (glasses < TARGET_GLASSES) {
      const newCount = glasses + 1;
      setGlasses(newCount);
      saveToStorage(newCount);
    }
  };

  const removeGlass = () => {
    if (glasses > 0) {
      const newCount = glasses - 1;
      setGlasses(newCount);
      saveToStorage(newCount);
    }
  };

  const resetWater = () => {
    setGlasses(0);
    const storageKey = `water_${currentDate}`;
    localStorage.removeItem(storageKey);
  };

  return (
    <div className="glass card water-tracker-card">
      <div
        className="water-tracker-header"
      >
        <div className="water-tracker-title-row">
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"></path>
          </svg>
          <h3
            className="water-tracker-title"
          >
            Вода
          </h3>
        </div>
        <button
          onClick={resetWater}
          className="glass-btn water-tracker-reset-btn"
          title="Сбросить"
        >
          Сброс
        </button>
      </div>

      <div
        className="water-tracker-count"
      >
        {glasses} из {TARGET_GLASSES} стаканов
      </div>

      <div className="water-tracker-glasses">
        {Array.from({ length: TARGET_GLASSES }, (_, i) => (
          <button
            key={i}
            onClick={i < glasses ? removeGlass : addGlass}
            className="water-tracker-glass-btn"
            data-filled={i < glasses}
            title={i < glasses ? "Убрать стакан" : "Добавить стакан"}
          >
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              fill={i < glasses ? "var(--macro-protein-color)" : "none"}
              stroke="var(--macro-protein-color)"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"></path>
            </svg>
          </button>
        ))}
      </div>
    </div>
  );
}
