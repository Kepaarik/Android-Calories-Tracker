// frontend/src/hooks/useTouchDrag.ts
import { useRef, useState, useEffect } from "react";

interface UseTouchDragOptions {
  onReorder: (fromIndex: number, toIndex: number) => void;
}

export function useTouchDrag({ onReorder }: UseTouchDragOptions) {
  const [dragIndex, setDragIndex] = useState<number | null>(null);
  const [overIndex, setOverIndex] = useState<number | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const dragIndexRef = useRef<number | null>(null);
  const overIndexRef = useRef<number | null>(null);

  useEffect(() => {
    dragIndexRef.current = dragIndex;
  }, [dragIndex]);

  useEffect(() => {
    overIndexRef.current = overIndex;
  }, [overIndex]);

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const getTouchIndex = (clientY: number): number | null => {
      const items = Array.from(container.querySelectorAll("[data-drag-item]"));
      for (let i = 0; i < items.length; i++) {
        const rect = items[i].getBoundingClientRect();
        if (clientY >= rect.top && clientY <= rect.bottom) {
          return i;
        }
      }
      return null;
    };

    const handleTouchStart = (e: TouchEvent) => {
      const target = e.target as HTMLElement;

      // ← НЕ запускаем drag если касание на кнопке переключателя
      if (target.closest("button")) {
        return;
      }

      const dragItem = target.closest("[data-drag-item]") as HTMLElement;
      if (!dragItem) return;

      const items = Array.from(container.querySelectorAll("[data-drag-item]"));
      const index = items.indexOf(dragItem);
      if (index === -1) return;

      e.preventDefault();
      dragIndexRef.current = index;
      overIndexRef.current = index;
      setDragIndex(index);
      setOverIndex(index);
    };

    const handleTouchMove = (e: TouchEvent) => {
      if (dragIndexRef.current === null) return;
      e.preventDefault();

      const touch = e.touches[0];
      const newIndex = getTouchIndex(touch.clientY);
      if (newIndex !== null && newIndex !== overIndexRef.current) {
        overIndexRef.current = newIndex;
        setOverIndex(newIndex);
      }
    };

    const handleTouchEnd = (e: TouchEvent) => {
      if (dragIndexRef.current === null) return;
      e.preventDefault();

      if (
        dragIndexRef.current !== null &&
        overIndexRef.current !== null &&
        dragIndexRef.current !== overIndexRef.current
      ) {
        onReorder(dragIndexRef.current, overIndexRef.current);
      }
      dragIndexRef.current = null;
      overIndexRef.current = null;
      setDragIndex(null);
      setOverIndex(null);
    };

    container.addEventListener("touchstart", handleTouchStart, {
      passive: false,
    });
    container.addEventListener("touchmove", handleTouchMove, {
      passive: false,
    });
    container.addEventListener("touchend", handleTouchEnd, { passive: false });

    return () => {
      container.removeEventListener("touchstart", handleTouchStart);
      container.removeEventListener("touchmove", handleTouchMove);
      container.removeEventListener("touchend", handleTouchEnd);
    };
  }, [onReorder]);

  return {
    containerRef,
    dragIndex,
    overIndex,
  };
}
