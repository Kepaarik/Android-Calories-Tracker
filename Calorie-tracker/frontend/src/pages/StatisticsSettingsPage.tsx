import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useStatisticsSettings } from "../hooks/useStatisticsSettings";
import StatisticsWidgetsList from "../components/settings/StatisticsWidgetsList";
import { StatWidgetConfig } from "../types/api";
import PageHeader from "../components/ui/PageHeader";
import Skeleton from "../components/ui/Skeleton";
import "./StatisticsSettingsPage.css";

export default function StatisticsSettingsPage() {
  const navigate = useNavigate();
  const { settings, isLoading, error, saveSettings } = useStatisticsSettings();

  const [localWidgets, setLocalWidgets] = useState<StatWidgetConfig[]>([]);
  const [isSaving, setIsSaving] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);

  useEffect(() => {
    if (settings?.widgets) {
      setLocalWidgets([...settings.widgets].sort((a, b) => a.order - b.order));
      setHasChanges(false);
    }
  }, [settings]);

  const handleToggleVisibility = (widgetId: string) => {
    setLocalWidgets((prev) => {
      const newWidgets = prev.map((widget) =>
        widget.id === widgetId
          ? { ...widget, visible: !widget.visible }
          : widget
      );
      setHasChanges(true);
      return newWidgets;
    });
  };

  const handleReorder = (fromIndex: number, toIndex: number) => {
    const newWidgets = [...localWidgets];
    const [moved] = newWidgets.splice(fromIndex, 1);
    newWidgets.splice(toIndex, 0, moved);

    const updated = newWidgets.map((widget, index) => ({
      ...widget,
      order: index,
    }));

    setLocalWidgets(updated);
    setHasChanges(true);
  };

  const handleSave = async () => {
    setIsSaving(true);
    const success = await saveSettings({ widgets: localWidgets });
    setIsSaving(false);

    if (success) {
      navigate(-1);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  if (isLoading) {
    return (
      <div className="container statistics-settings-page-container">
        <div className="statistics-settings-page-skeleton-list">
          {[1, 2, 3, 4].map((i) => (
            <Skeleton key={i} variant="rect" width="100%" height="56px" style={{ borderRadius: "10px" }} />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="container statistics-settings-page-container">
      <PageHeader
        title="Порядок виджетов"
        subtitle="Перетаскивайте, чтобы изменить порядок"
        onBack={handleCancel}
      />

      {error && (
        <div
          className="glass card statistics-settings-page-error-banner"
        >
          {error}
        </div>
      )}

      <StatisticsWidgetsList
        widgets={localWidgets}
        onToggleVisibility={handleToggleVisibility}
        onReorder={handleReorder}
      />

      <div className="statistics-settings-page-actions">
        <button
          onClick={handleSave}
          disabled={isSaving || !hasChanges}
          className="glass-btn glass-btn-success statistics-settings-page-save-btn"
          style={{ opacity: !hasChanges ? 0.5 : 1 }}
        >
          {isSaving ? "Сохранение..." : "Сохранить"}
        </button>

        <button
          onClick={handleCancel}
          className="glass-btn statistics-settings-page-cancel-btn"
        >
          Отмена
        </button>
      </div>
    </div>
  );
}
