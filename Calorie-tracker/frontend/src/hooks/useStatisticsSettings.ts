import { useState, useEffect, useCallback } from "react";
import { statisticsSettingsApi } from "../api/endpoints";
import { StatWidgetConfig, StatisticsSettings } from "../types/api";

export const DEFAULT_STAT_WIDGETS: StatWidgetConfig[] = [
  { id: "summary", visible: true, order: 0 },
  { id: "water", visible: true, order: 1 },
  { id: "weekly_stats", visible: true, order: 2 },
  { id: "weight", visible: true, order: 3 },
];

const reconcileWithDefaults = (widgets: StatWidgetConfig[]): StatWidgetConfig[] => {
  const validIds = new Set(DEFAULT_STAT_WIDGETS.map((w) => w.id));
  // Отбрасываем виджеты, оставшиеся от старых версий списка (например add_entry, meals)
  const known = widgets.filter((w) => validIds.has(w.id));

  const knownIds = new Set(known.map((w) => w.id));
  const missing = DEFAULT_STAT_WIDGETS.filter((w) => !knownIds.has(w.id));

  let nextOrder = known.length;
  return [...known, ...missing.map((w) => ({ ...w, order: nextOrder++ }))];
};

export function useStatisticsSettings() {
  const [settings, setSettings] = useState<StatisticsSettings | null>({
    widgets: DEFAULT_STAT_WIDGETS,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setIsLoading(true);
    try {
      const res = await statisticsSettingsApi.getSettings();
      setSettings({ widgets: reconcileWithDefaults(res.data.widgets || []) });
      setError(null);
    } catch (err: any) {
      setSettings({ widgets: DEFAULT_STAT_WIDGETS });
      const detail = err.response?.data?.detail;
      setError(
        detail
          ? `Не удалось загрузить порядок виджетов: ${detail}`
          : "Не удалось загрузить порядок виджетов, используются значения по умолчанию"
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const saveSettings = async (newSettings: StatisticsSettings): Promise<boolean> => {
    try {
      await statisticsSettingsApi.saveSettings(newSettings);
      setSettings(newSettings);
      setError(null);
      return true;
    } catch (err: any) {
      setError(err.response?.data?.detail || "Ошибка сохранения порядка виджетов");
      return false;
    }
  };

  const resetToDefaults = async () => {
    const defaults: StatisticsSettings = { widgets: DEFAULT_STAT_WIDGETS };
    return saveSettings(defaults);
  };

  return { settings, isLoading, error, saveSettings, resetToDefaults, refresh: load };
}
