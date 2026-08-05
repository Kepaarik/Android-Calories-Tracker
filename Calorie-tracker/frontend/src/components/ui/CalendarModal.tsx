import { useState, useEffect } from "react";
import { format, startOfMonth, endOfMonth, eachDayOfInterval, isSameMonth, isSameDay, addMonths, subMonths } from "date-fns";
import { ru } from "date-fns/locale";
import Modal from "./Modal";
import GlassButton from "./GlassButton";
import Icon from "./Icon";
import "./CalendarModal.css";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  selectedDate: string;
  onDateSelect: (date: string) => void;
}

const WEEK_DAYS = ["пн", "вт", "ср", "чт", "пт", "сб", "вс"];

export default function CalendarModal({ isOpen, onClose, selectedDate, onDateSelect }: Props) {
  const [currentMonth, setCurrentMonth] = useState(new Date(selectedDate));

  useEffect(() => {
    if (isOpen) setCurrentMonth(new Date(selectedDate));
  }, [isOpen, selectedDate]);

  const monthStart = startOfMonth(currentMonth);
  const monthEnd = endOfMonth(currentMonth);
  const days = eachDayOfInterval({ start: monthStart, end: monthEnd });

  const handleDateClick = (day: Date) => {
    onDateSelect(format(day, "yyyy-MM-dd"));
    onClose();
  };

  const handleToday = () => {
    onDateSelect(format(new Date(), "yyyy-MM-dd"));
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} maxWidth="350px" >
      <div className="calendar-modal-header">
        <GlassButton variant="icon" onClick={() => setCurrentMonth(subMonths(currentMonth, 1))}>
          <Icon name="back" size={16} />
        </GlassButton>
        <h3 className="calendar-modal-title">
          {format(currentMonth, "LLLL yyyy", { locale: ru })}
        </h3>
        <GlassButton variant="icon" onClick={() => setCurrentMonth(addMonths(currentMonth, 1))}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="9 18 15 12 9 6" />
          </svg>
        </GlassButton>
      </div>

      <GlassButton onClick={handleToday} fullWidth className="calendar-modal-today-btn">
        Сегодня
      </GlassButton>

      <div className="calendar-modal-weekdays">
        {WEEK_DAYS.map((day) => (
          <div key={day} className="calendar-modal-weekday-label">
            {day}
          </div>
        ))}
      </div>

      <div className="calendar-modal-days-grid">
        {days.map((day, index) => {
          const isSelected = isSameDay(day, new Date(selectedDate));
          const isToday = isSameDay(day, new Date());
          const isCurrentMonth = isSameMonth(day, currentMonth);

          return (
            <button
              key={index}
              onClick={() => handleDateClick(day)}
              className="calendar-modal-day-btn"
              style={{
                fontWeight: isSelected ? "bold" : "500",
                color: isSelected ? "white" : isToday ? "var(--primary-color)" : isCurrentMonth ? "var(--text-primary)" : "var(--text-secondary)",
                background: isSelected ? "var(--primary-color)" : isToday ? "var(--primary-light, rgba(0, 136, 204, 0.1))" : "transparent",
                border: isToday && !isSelected ? "1px solid var(--primary-color)" : "none",
                opacity: isCurrentMonth ? 1 : 0.4,
              }}
            >
              {format(day, "d")}
            </button>
          );
        })}
      </div>

      <GlassButton onClick={onClose} fullWidth className="calendar-modal-close-btn">
        Закрыть
      </GlassButton>
    </Modal>
  );
}