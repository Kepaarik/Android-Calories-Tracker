// frontend/src/components/dashboard/WeightTrackerWidget.tsx
import { useState, useEffect } from "react";
import { weightApi } from "../../api/endpoints";
import { WeightEntry, WeightStats } from "../../types/api";
import { format, parseISO } from "date-fns";
import { ru } from "date-fns/locale";
import StatCard from "../ui/StatCard";
import Icon from "../ui/Icon";
import { useToast } from "../../context/ToastContext";
import { useConfirm } from "../../context/ConfirmContext";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ReferenceLine,
} from "recharts";
import CalendarModal from "../ui/CalendarModal";
import Skeleton from "../ui/Skeleton";
import "./WeightTrackerWidget.css";

export default function WeightTrackerWidget() {
  const [entries, setEntries] = useState<WeightEntry[]>([]);
  const [stats, setStats] = useState<WeightStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAdding, setIsAdding] = useState(false);
  const [newWeight, setNewWeight] = useState("");
  const [newDate, setNewDate] = useState(format(new Date(), "yyyy-MM-dd"));
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const toast = useToast();
  const { confirm } = useConfirm();
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [entriesRes, statsRes] = await Promise.all([
        weightApi.getEntries(50),
        weightApi.getStats(),
      ]);
      setEntries(entriesRes.data);
      setStats(statsRes.data);
    } catch (err) {
      console.error("Ошибка загрузки веса:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAdd = async () => {
    const weight = parseFloat(newWeight);
    if (isNaN(weight) || weight < 30 || weight > 300) {
      toast.warning("Введите корректный вес (30-300 кг)");
      return;
    }
    try {
      await weightApi.addEntry({ weight_kg: weight, date: newDate });
      setNewWeight("");
      setIsAdding(false);
      loadData();
      toast.success("Запись веса добавлена", `${weight} кг`);
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка добавления");
    }
  };

  const handleDelete = async (entryId: number) => {
    const entry = entries.find((e) => e.id === entryId);
    if (!entry) return;

    const ok = await confirm({
      title: "Удаление записи",
      message: `Удалить запись "${entry.weight_kg} кг" от ${format(
        parseISO(entry.recorded_at),
        "dd.MM.yyyy"
      )}?`,
      confirmText: "Удалить",
      variant: "danger",
    });
    if (!ok) return;

    // Optimistic UI update
    setEntries(entries.filter((e) => e.id !== entryId));

    try {
      await weightApi.deleteEntry(entryId);
      toast.success("Запись удалена", `${entry.weight_kg} кг`);
    } catch (err: any) {
      // Откат
      setEntries((prev) => [...prev, entry]);
      toast.error(err.response?.data?.detail || "Ошибка удаления");
    }
  };

  const chartData = entries
    .slice(0, 30)
    .reverse()
    .map((entry) => ({
      date: format(parseISO(entry.recorded_at), "dd.MM"),
      weight: entry.weight_kg,
    }));

  const getTrend = () => {
    if (entries.length < 2) return null;
    const sorted = [...entries].sort(
      (a, b) =>
        new Date(b.recorded_at).getTime() - new Date(a.recorded_at).getTime()
    );
    return sorted[0].weight_kg - sorted[1].weight_kg;
  };

  const trend = getTrend();

  if (isLoading) {
    return (
      <div
        className="glass card weight-tracker-widget-card"
      >
        <div
          className="weight-tracker-widget-header"
        >
          <Skeleton variant="text" width="150px" height="24px" />
          <Skeleton
            variant="rect"
            width="36px"
            height="36px"
            className="weight-tracker-widget-skeleton-avatar"
          />
        </div>

        {/* Скелетон статистики */}
        <div
          className="weight-tracker-widget-skeleton-stats-grid"
        >
          {[1, 2, 3, 4].map((i) => (
            <Skeleton key={i} variant="card" height="80px" />
          ))}
        </div>

        {/* Скелетон графика */}
        <Skeleton
          variant="rect"
          height="250px"
          className="weight-tracker-widget-skeleton-chart"
        />

        {/* Скелетон списка записей */}
        <div className="weight-tracker-widget-skeleton-entries">
          <Skeleton variant="text" width="120px" height="16px" />
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} variant="card" height="60px" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <>
      <div
        className="glass card weight-tracker-widget-card"
      >
        {/* Header с анимированным плюсиком/крестиком */}
        <div
          className="weight-tracker-widget-header"
        >
          <h3
            className="weight-tracker-widget-title"
          >
            Отслеживание веса
          </h3>
          <button
            onClick={() => setIsAdding(!isAdding)}
            className="glass-btn btn-icon weight-tracker-widget-add-button"
            style={{
              transform: isAdding ? "rotate(45deg)" : "rotate(0deg)",
            }}
            title={isAdding ? "Закрыть" : "Добавить запись"}
          >
            <Icon name="plus" size={18} />
          </button>
        </div>

        {/* Форма добавления с анимацией появления */}
        <div
          className="weight-tracker-widget-form-wrapper"
          style={{
            maxHeight: isAdding ? "500px" : "0",
            opacity: isAdding ? 1 : 0,
            marginBottom: isAdding ? "16px" : "0",
          }}
        >
          <div
            className="weight-tracker-widget-form-inner"
            style={{
              transform: isAdding ? "translateY(0)" : "translateY(-10px)",
            }}
          >
            <div
              className="weight-tracker-widget-form-fields"
            >
              {/* Поле веса */}
              <div
                className="weight-tracker-widget-form-field-weight"
                style={{
                  opacity: isAdding ? 1 : 0,
                  transform: isAdding ? "translateX(0)" : "translateX(-20px)",
                }}
              >
                <label
                  className="weight-tracker-widget-form-label"
                >
                  Вес (кг)
                </label>
                <input
                  type="number"
                  className="glass-input weight-tracker-widget-weight-input"
                  value={newWeight}
                  onChange={(e) => setNewWeight(e.target.value)}
                  placeholder="70.5"
                  min="30"
                  max="300"
                  step="0.1"
                  //autoFocus
                />
              </div>

              {/* Поле даты */}
              <div
                className="weight-tracker-widget-form-field-date"
                style={{
                  opacity: isAdding ? 1 : 0,
                  transform: isAdding ? "translateX(0)" : "translateX(-20px)",
                }}
              >
                <label
                  className="weight-tracker-widget-form-label"
                >
                  Дата
                </label>
                <div
                  className="weight-tracker-widget-date-row"
                >
                  <button
                    type="button"
                    onClick={() => setIsCalendarOpen(true)}
                    className="glass-btn weight-tracker-widget-date-button"
                  >
                    {format(parseISO(newDate), "dd.MM.yyyy")}
                  </button>
                  <button
                    type="button"
                    onClick={() => setIsCalendarOpen(true)}
                    className="glass-btn btn-icon weight-tracker-widget-date-icon-button"
                  >
                    <Icon name="calendar" size={18} />
                  </button>
                </div>
              </div>

              {/* Кнопки действий */}
              <div
                className="weight-tracker-widget-form-actions"
                style={{
                  opacity: isAdding ? 1 : 0,
                  transform: isAdding ? "translateY(0)" : "translateY(10px)",
                }}
              >
                <button
                  onClick={handleAdd}
                  className="glass-btn glass-btn-success weight-tracker-widget-add-submit"
                >
                  Добавить
                </button>
                <button
                  onClick={() => setIsAdding(false)}
                  className="glass-btn weight-tracker-widget-cancel-button"
                >
                  Отмена
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Статистика */}
        {stats && stats.current_weight && (
          <div className="weight-tracker-widget-stats-section">
            <div className="weight-stats-grid">
              <div className="glass stat-card">
                <div className="stat-label">Текущий</div>
                <div
                  className="stat-value weight-tracker-widget-stat-current"
                >
                  {stats.current_weight.toFixed(1)}
                </div>
                <div className="stat-subtitle">кг</div>
              </div>
              <div className="glass stat-card">
                <div className="stat-label">Изменение</div>
                <div
                  className="stat-value"
                  style={{
                    color:
                      trend !== null
                        ? trend > 0
                          ? "var(--danger-color)"
                          : "var(--success-color)"
                        : "var(--text-secondary)",
                  }}
                >
                  {trend !== null
                    ? (trend > 0 ? "+" : "") + trend.toFixed(1)
                    : "—"}
                </div>
                <div className="stat-subtitle">кг</div>
              </div>
              <div className="glass stat-card">
                <div className="stat-label">Минимум</div>
                <div
                  className="stat-value weight-tracker-widget-stat-min"
                >
                  {stats.min_weight?.toFixed(1) || "—"}
                </div>
                <div className="stat-subtitle">кг</div>
              </div>
              <div className="glass stat-card">
                <div className="stat-label">Максимум</div>
                <div
                  className="stat-value weight-tracker-widget-stat-max"
                >
                  {stats.max_weight?.toFixed(1) || "—"}
                </div>
                <div className="stat-subtitle">кг</div>
              </div>
            </div>
          </div>
        )}

        {/* График */}
        {chartData.length > 0 && (
          <div className="weight-tracker-widget-chart-section">
            <div
              className="weight-tracker-widget-chart-title"
            >
              Динамика веса
            </div>
            <div className="weight-tracker-widget-chart-container">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient
                      id="weightGradient"
                      x1="0"
                      y1="0"
                      x2="0"
                      y2="1"
                    >
                      <stop
                        offset="5%"
                        stopColor="var(--primary-color)"
                        stopOpacity={0.3}
                      />
                      <stop
                        offset="95%"
                        stopColor="var(--primary-color)"
                        stopOpacity={0}
                      />
                    </linearGradient>
                  </defs>
                  <CartesianGrid
                    stroke="var(--border-color)"
                    strokeDasharray="3 3"
                    vertical={false}
                    opacity={0.5}
                  />
                  <XAxis
                    dataKey="date"
                    stroke="var(--text-secondary)"
                    style={{ fontSize: "11px" }}
                    tickLine={false}
                    axisLine={false}
                  />
                  <YAxis
                    stroke="var(--text-secondary)"
                    style={{ fontSize: "11px" }}
                    tickLine={false}
                    axisLine={false}
                    domain={["dataMin - 2", "dataMax + 2"]}
                    tickFormatter={(value) => `${value}`}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "var(--surface-color)",
                      border: "1px solid var(--border-color)",
                      borderRadius: "8px",
                      color: "var(--text-primary)",
                    }}
                    formatter={(value: any) => [
  `${Number(value ?? 0).toFixed(1)} кг`,
  "Вес",
]}
                  />
                  <ReferenceLine
                    y={stats?.current_weight ?? undefined}
                    stroke="var(--primary-color)"
                    strokeDasharray="3 3"
                  />
                  <Area
                    type="monotone"
                    dataKey="weight"
                    stroke="var(--primary-color)"
                    strokeWidth={3}
                    fill="url(#weightGradient)"
                    dot={{
                      fill: "var(--primary-color)",
                      stroke: "var(--surface-color)",
                      strokeWidth: 2,
                      r: 4,
                    }}
                    activeDot={{
                      r: 6,
                      fill: "var(--primary-color)",
                      stroke: "var(--surface-color)",
                      strokeWidth: 2,
                    }}
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* Список записей */}
        {entries.length === 0 ? (
          <div
            className="weight-tracker-widget-empty-entries"
          >
            Нет записей о весе. Нажмите + чтобы добавить.
          </div>
        ) : (
          <div className="weight-tracker-widget-entries-list">
            <div
              className="weight-tracker-widget-entries-title"
            >
              Последние записи:
            </div>
            {entries.slice(0, 5).map((entry) => (
              <div
                key={entry.id}
                className="weight-tracker-widget-entry-row"
              >
                <div>
                  <div
                    className="weight-tracker-widget-entry-weight"
                  >
                    {entry.weight_kg.toFixed(1)} кг
                  </div>
                  <div
                    className="weight-tracker-widget-entry-date"
                  >
                    {format(parseISO(entry.recorded_at), "d MMMM yyyy", {
                      locale: ru,
                    })}
                  </div>
                </div>
                <button
                  onClick={() => handleDelete(entry.id)}
                  className="glass-btn btn-icon weight-tracker-widget-delete-button"
                  title="Удалить"
                >
                  <Icon name="trash" size={16} />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <CalendarModal
        isOpen={isCalendarOpen}
        onClose={() => setIsCalendarOpen(false)}
        selectedDate={newDate}
        onDateSelect={(date) => {
          setNewDate(date);
          setIsCalendarOpen(false);
        }}
      />
    </>
  );
}
