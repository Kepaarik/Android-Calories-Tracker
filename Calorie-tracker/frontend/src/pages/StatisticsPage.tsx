import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { diaryApi, profileApi } from "../api/endpoints";
import { DailySummary } from "../types/api";
import PageHeader from "../components/ui/PageHeader";
import GlassButton from "../components/ui/GlassButton";
import Icon from "../components/ui/Icon";
import DailySummaryWidget from "../components/dashboard/DailySummaryWidget";
import WaterTrackerWidget from "../components/dashboard/WaterTrackerWidget";
import WeightTrackerWidget from "../components/dashboard/WeightTrackerWidget";
import WeeklyStats from "../components/stats/WeeklyStats";
import Skeleton from "../components/ui/Skeleton";
import { useStatisticsSettings } from "../hooks/useStatisticsSettings";
import { useSelectedDateStore } from "../store/selectedDateStore";
import "./StatisticsPage.css";

export default function StatisticsPage() {
  const navigate = useNavigate();
  const { selectedDate } = useSelectedDateStore();
  const { settings } = useStatisticsSettings();

  const [summary, setSummary] = useState<DailySummary | null>(null);
  const [dailyCalorieGoal, setDailyCalorieGoal] = useState(2000);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      setIsLoading(true);
      try {
        const [summaryRes, profileRes] = await Promise.all([
          diaryApi.getSummary(selectedDate),
          profileApi.getProfile(),
        ]);
        setSummary(summaryRes.data);
        setDailyCalorieGoal(profileRes.data.calculated_calories || 2000);
      } catch (err) {
        console.error("Ошибка загрузки статистики:", err);
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, [selectedDate]);

  const renderWidget = (widgetId: string) => {
    switch (widgetId) {
      case "summary":
        return isLoading ? (
          <Skeleton key={widgetId} variant="card" height="180px" />
        ) : (
          summary && (
            <DailySummaryWidget
              key={widgetId}
              summary={summary}
              dailyCalorieGoal={dailyCalorieGoal}
            />
          )
        );
      case "water":
        return <WaterTrackerWidget key={widgetId} date={selectedDate} />;
      case "weekly_stats":
        return <WeeklyStats key={widgetId} />;
      case "weight":
        return <WeightTrackerWidget key={widgetId} />;
      default:
        return null;
    }
  };

  const orderedWidgetIds = (settings?.widgets || [])
    .filter((w) => w.visible)
    .sort((a, b) => a.order - b.order)
    .map((w) => w.id);

  return (
    <div className="container statistics-page-container">
      <PageHeader
        title="Статистика"
        onBack={() => navigate(-1)}
        actions={
          <GlassButton
            variant="icon"
            onClick={() => navigate("/statistics-settings")}
            title="Порядок виджетов"
          >
            <Icon name="settings" size={20} />
          </GlassButton>
        }
      />

      {orderedWidgetIds.map((id) => (
        <div key={id} className="statistics-page-widget-wrapper">
          {renderWidget(id)}
        </div>
      ))}
    </div>
  );
}
