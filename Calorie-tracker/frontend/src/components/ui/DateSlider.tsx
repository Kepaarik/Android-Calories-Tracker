import { useState, useEffect } from "react";
import { format, addDays, isSameDay } from "date-fns";
import { ru } from "date-fns/locale";
import "./DateSlider.css";

interface DateSliderProps {
  selectedDate: string;
  onDateChange: (date: string) => void;
}

export default function DateSlider({
  selectedDate,
  onDateChange,
}: DateSliderProps) {
  const today = new Date();
  const [centerDate, setCenterDate] = useState(new Date(selectedDate));
  const [animationDirection, setAnimationDirection] = useState<
    "left" | "right" | null
  >(null);
  const [visibleDays, setVisibleDays] = useState(9);

  // Определяем количество видимых дней в зависимости от ширины экрана
  useEffect(() => {
    const updateVisibleDays = () => {
      const width = window.innerWidth;
      if (width < 360) {
        setVisibleDays(5); // Очень маленькие экраны (было 3)
      } else if (width < 480) {
        setVisibleDays(7); // Маленькие экраны (было 5)
      } else if (width < 768) {
        setVisibleDays(9); // Средние экраны (было 7)
      } else {
        setVisibleDays(11); // Большие экраны (было 9)
      }
    };

    updateVisibleDays();
    window.addEventListener("resize", updateVisibleDays);
    return () => window.removeEventListener("resize", updateVisibleDays);
  }, []);

  // Обновляем центральную дату при изменении выбранной
  useEffect(() => {
    const newCenter = new Date(selectedDate);
    const diff = newCenter.getTime() - centerDate.getTime();
    if (diff > 0) {
      setAnimationDirection("left");
    } else if (diff < 0) {
      setAnimationDirection("right");
    }
    setCenterDate(newCenter);

    const timer = setTimeout(() => {
      setAnimationDirection(null);
    }, 300);
    return () => clearTimeout(timer);
  }, [selectedDate]);

  const dates = Array.from({ length: visibleDays }, (_, i) => {
    return addDays(centerDate, i - Math.floor(visibleDays / 2));
  });

  const isToday = (date: Date) => isSameDay(date, today);
  const isSelected = (date: Date) => isSameDay(date, new Date(selectedDate));

  const handleDateClick = (date: Date) => {
    const newDateStr = format(date, "yyyy-MM-dd");
    onDateChange(newDateStr);
  };

  // Адаптивные размеры в зависимости от ширины экрана
  const isSmallScreen =
    typeof window !== "undefined" && window.innerWidth < 480;

  return (
    <div
      className="glass card date-slider-card"
      style={{
        padding: isSmallScreen ? "8px 6px" : "12px 8px",
      }}
    >
      <div
        className="date-slider-row"
        style={{
          gap: isSmallScreen ? "6px" : "8px",
        }}
      >
        {dates.map((date, index) => {
          const selected = isSelected(date);
          const todayFlag = isToday(date);

          return (
            <button
              key={index}
              onClick={() => handleDateClick(date)}
              data-selected={selected}
              className="date-button date-slider-day"
              style={{
                minWidth: isSmallScreen ? "38px" : "45px",
                maxWidth: isSmallScreen ? "55px" : "65px",
                // Уменьшенные отступы
                padding: selected
                  ? isSmallScreen
                    ? "14px 4px"
                    : "18px 6px"
                  : isSmallScreen
                  ? "10px 4px"
                  : "14px 6px",
                borderRadius: isSmallScreen ? "10px" : "12px",
                gap: isSmallScreen ? "4px" : "6px",
                background: selected
                  ? "var(--glass-highlight)"
                  : "var(--glass-bg)",
                border: selected
                  ? "1px solid var(--glass-border)"
                  : "1px solid var(--border-color)",
                backdropFilter: selected
                  ? "blur(15px) saturate(180%)"
                  : "blur(10px) saturate(150%)",
                WebkitBackdropFilter: selected
                  ? "blur(15px) saturate(180%)"
                  : "blur(10px) saturate(150%)",
                boxShadow: selected
                  ? "0 4px 16px var(--shadow-strong), inset 0 1px 0 rgba(255,255,255,0.2)"
                  : "0 2px 8px var(--shadow-color)",
                color: selected
                  ? "var(--text-primary)"
                  : "var(--text-secondary)",
                fontWeight: selected ? "700" : "500",
                animation: animationDirection
                  ? `slide${
                      animationDirection === "left" ? "FromRight" : "FromLeft"
                    } 0.3s cubic-bezier(0.4, 0, 0.2, 1)`
                  : "none",
              }}
            >
              <span
                className="date-slider-weekday"
                style={{
                  fontSize: isSmallScreen ? "9px" : "10px",
                }}
              >
                {format(date, "EEE", { locale: ru }).slice(0, 3)}
              </span>
              <span
                className="date-slider-daynum"
                style={{
                  fontSize: isSmallScreen ? "18px" : "22px",
                  fontWeight: selected ? "700" : "500",
                }}
              >
                {format(date, "d")}
              </span>
              {todayFlag && (
                <span
                  className="date-slider-today-label"
                  style={{
                    fontSize: isSmallScreen ? "8px" : "9px",
                    color: selected
                      ? "var(--text-primary)"
                      : "var(--primary-color)",
                  }}
                >
                  Сегодня
                </span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
