// frontend/src/components/dashboard/WaterTrackerWidget.tsx
import WaterTracker from "../tracker/WaterTracker";
import Skeleton from "../ui/Skeleton";
import { useState, useEffect } from "react";
import "./WaterTrackerWidget.css";

interface WaterTrackerWidgetProps {
  date?: string;
}

export default function WaterTrackerWidget({ date }: WaterTrackerWidgetProps) {
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Имитация загрузки (можно убрать, если WaterTracker сам загружает данные)
    const timer = setTimeout(() => setIsLoading(false), 300);
    return () => clearTimeout(timer);
  }, []);

  if (isLoading) {
    return (
      <div
        className="glass card water-tracker-widget-skeleton-card"
      >
        <div
          className="water-tracker-widget-skeleton-header"
        >
          <div className="water-tracker-widget-skeleton-header-left">
            <Skeleton variant="rect" width="20px" height="20px" />
            <Skeleton variant="text" width="80px" height="20px" />
          </div>
          <Skeleton variant="rect" width="60px" height="24px" />
        </div>
        <Skeleton
          variant="text"
          width="150px"
          height="16px"
          className="water-tracker-widget-skeleton-subtitle"
        />
        <div className="water-tracker-widget-skeleton-dots">
          {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => (
            <Skeleton
              key={i}
              variant="rect"
              width="28px"
              height="28px"
              className="water-tracker-widget-skeleton-dot"
            />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div>
      <WaterTracker date={date} />
    </div>
  );
}
