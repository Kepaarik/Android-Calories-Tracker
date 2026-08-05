import { useState } from "react";
import { StatWidgetConfig } from "../../types/api";
import { WidgetIcon } from "../icons/WidgetIcons";
import { useTouchDrag } from "../../hooks/useTouchDrag";
import "./StatisticsWidgetsList.css";

const WIDGET_LABELS: Record<string, string> = {
  summary: "Сводка за день",
  water: "Вода",
  weekly_stats: "Статистика за неделю",
  weight: "Отслеживание веса",
};

interface SortableWidgetItemProps {
  widget: StatWidgetConfig;
  index: number;
  onToggleVisibility: (id: string) => void;
  onDragStart: (index: number, e: React.DragEvent) => void;
  onDragOver: (index: number, e: React.DragEvent) => void;
  onDragEnd: () => void;
  isDragging: boolean;
  isOver: boolean;
}

function SortableWidgetItem({
  widget,
  index,
  onToggleVisibility,
  onDragStart,
  onDragOver,
  onDragEnd,
  isDragging,
  isOver,
}: SortableWidgetItemProps) {
  return (
    <div
      data-drag-item
      draggable
      onDragStart={(e) => onDragStart(index, e)}
      onDragOver={(e) => onDragOver(index, e)}
      onDragEnd={onDragEnd}
      className="widget-item"
      data-dragging={isDragging}
      data-over={isOver}
    >
      <div
        data-drag-handle
        className="widget-drag-handle"
      >
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
        >
          <line x1="8" y1="6" x2="21" y2="6"></line>
          <line x1="8" y1="12" x2="21" y2="12"></line>
          <line x1="8" y1="18" x2="21" y2="18"></line>
          <line x1="3" y1="6" x2="3.01" y2="6"></line>
          <line x1="3" y1="12" x2="3.01" y2="12"></line>
          <line x1="3" y1="18" x2="3.01" y2="18"></line>
        </svg>
      </div>

      <div
        className="widget-icon-wrapper"
      >
        <WidgetIcon widgetId={widget.id} size={20} />
      </div>

      <div
        className="widget-label"
      >
        {WIDGET_LABELS[widget.id] || widget.id}
      </div>

      <button
        onClick={(e) => {
          e.stopPropagation();
          onToggleVisibility(widget.id);
        }}
        className="widget-toggle-btn"
        data-visible={widget.visible}
      >
        <div
          className="widget-toggle-thumb"
        />
      </button>
    </div>
  );
}

interface StatisticsWidgetsListProps {
  widgets: StatWidgetConfig[];
  onToggleVisibility: (id: string) => void;
  onReorder: (fromIndex: number, toIndex: number) => void;
}

export default function StatisticsWidgetsList({
  widgets,
  onToggleVisibility,
  onReorder,
}: StatisticsWidgetsListProps) {
  const [dragIndexDesktop, setDragIndexDesktop] = useState<number | null>(null);
  const [overIndexDesktop, setOverIndexDesktop] = useState<number | null>(null);

  const {
    containerRef,
    dragIndex: dragIndexTouch,
    overIndex: overIndexTouch,
  } = useTouchDrag({
    onReorder: (from, to) => {
      onReorder(from, to);
    },
  });

  // Объединяем touch и desktop индексы
  const dragIndex = dragIndexTouch ?? dragIndexDesktop;
  const overIndex = overIndexTouch ?? overIndexDesktop;

  const handleDragStart = (index: number, e: React.DragEvent) => {
    setDragIndexDesktop(index);
    e.dataTransfer.effectAllowed = "move";
  };

  const handleDragOver = (index: number, e: React.DragEvent) => {
    e.preventDefault();
    if (dragIndexDesktop !== null && dragIndexDesktop !== index) {
      setOverIndexDesktop(index);
    }
  };

  const handleDragEnd = () => {
    if (
      dragIndexDesktop !== null &&
      overIndexDesktop !== null &&
      dragIndexDesktop !== overIndexDesktop
    ) {
      onReorder(dragIndexDesktop, overIndexDesktop);
    }
    setDragIndexDesktop(null);
    setOverIndexDesktop(null);
  };

  return (
    <div ref={containerRef} className="widgets-list-container">
      {widgets.map((widget, index) => (
        <SortableWidgetItem
          key={widget.id}
          widget={widget}
          index={index}
          onToggleVisibility={onToggleVisibility}
          onDragStart={handleDragStart}
          onDragOver={handleDragOver}
          onDragEnd={handleDragEnd}
          isDragging={dragIndex === index}
          isOver={overIndex === index && dragIndex !== index}
        />
      ))}
    </div>
  );
}
