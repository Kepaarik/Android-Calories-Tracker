import { useState, useEffect } from "react";
import { diaryApi } from "../../api/endpoints";
import { format, subDays } from "date-fns";
import { ru } from "date-fns/locale";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import StatCard from "../ui/StatCard";
import Skeleton from "../ui/Skeleton";
import "./WeeklyStats.css";

export default function WeeklyStats() {
  const [weekData, setWeekData] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [streak, setStreak] = useState(0);

  const fetchWeekData = async () => {
    setIsLoading(true);
    try {
      const days = Array.from({ length: 7 }, (_, i) =>
        subDays(new Date(), i)
      ).reverse();

      const data = await Promise.all(
        days.map(async (day) => {
          const dateStr = format(day, "yyyy-MM-dd");
          try {
            const response = await diaryApi.getSummary(dateStr);
            // ← ИСПРАВЛЕНО: безопасное извлечение с fallback на total_* поля и 0
            const calories = response.data.calories ?? response.data.total_calories ?? 0;
            const proteins = response.data.proteins ?? response.data.total_proteins ?? 0;
            const fats = response.data.fats ?? response.data.total_fats ?? 0;
            const carbs = response.data.carbs ?? response.data.total_carbs ?? 0;

            return {
              date: format(day, "d"),
              fullDate: dateStr,
              calories: Math.round(calories),
              proteinsGrams: Math.round(proteins),
              fatsGrams: Math.round(fats),
              carbsGrams: Math.round(carbs),
              proteins: Math.round(proteins * 4),
              fats: Math.round(fats * 9),
              carbs: Math.round(carbs * 4),
            };
          } catch {
            return {
              date: format(day, "d"),
              fullDate: dateStr,
              calories: 0,
              proteinsGrams: 0,
              fatsGrams: 0,
              carbsGrams: 0,
              proteins: 0,
              fats: 0,
              carbs: 0,
            };
          }
        })
      );

      setWeekData(data);

      let currentStreak = 0;
      for (let i = data.length - 1; i >= 0; i--) {
        if (data[i].calories > 0) {
          currentStreak++;
        } else {
          break;
        }
      }
      setStreak(currentStreak);
    } catch (err) {
      console.error("Ошибка загрузки статистики:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchWeekData();

    const handleUpdate = () => fetchWeekData();
    window.addEventListener("diaryUpdated", handleUpdate);
    return () => window.removeEventListener("diaryUpdated", handleUpdate);
  }, []);

  const daysWithEntries = weekData.filter((d) => d.calories > 0);
  const avgCalories =
    daysWithEntries.length > 0
      ? Math.round(
          daysWithEntries.reduce((sum, d) => sum + d.calories, 0) /
            daysWithEntries.length
        )
      : 0;

  const totalCalories = weekData.reduce((sum, d) => sum + d.calories, 0);

  if (isLoading && weekData.length === 0) {
    return (
      <div className="glass card weekly-stats-card">
        <Skeleton
          variant="text"
          width="180px"
          height="24px"
          className="weekly-stats-skeleton-margin"
        />
        <Skeleton
          variant="rect"
          height="250px"
          className="weekly-stats-skeleton-chart"
        />
        <div
          className="weekly-stats-skeleton-grid"
        >
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} variant="card" height="70px" />
          ))}
        </div>
      </div>
    );
  }

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div
          className="glass card weekly-stats-tooltip"
        >
          <div
            className="weekly-stats-tooltip-date"
          >
            {data.fullDate &&
              format(new Date(data.fullDate), "d MMMM", { locale: ru })}
          </div>
          <div
            className="weekly-stats-tooltip-calories"
          >
            📊 Калории: {data.calories} ккал
          </div>
          <div
            className="weekly-stats-tooltip-proteins"
          >
            Белки: {data.proteinsGrams}г ({data.proteins} ккал)
          </div>
          <div
            className="weekly-stats-tooltip-fats"
          >
            Жиры: {data.fatsGrams}г ({data.fats} ккал)
          </div>
          <div className="weekly-stats-tooltip-carbs">
            Углеводы: {data.carbsGrams}г ({data.carbs} ккал)
          </div>
        </div>
      );
    }
    return null;
  };

  const maxCalories = Math.max(...weekData.map((d) => d.calories), 500);

  return (
    <div className="glass card weekly-stats-card">
      <h3
        className="weekly-stats-title"
      >
        Статистика за неделю
      </h3>

      <div className="weekly-stats-chart-wrapper">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={weekData}>
            <CartesianGrid
              stroke="var(--border-color)"
              strokeDasharray="3 3"
              vertical={false}
              opacity={0.5}
            />
            <XAxis
              dataKey="date"
              stroke="var(--text-secondary)"
              style={{ fontSize: "12px" }}
              tickLine={false}
              axisLine={false}
            />
            <YAxis
              stroke="var(--text-secondary)"
              style={{ fontSize: "11px" }}
              tickLine={false}
              axisLine={false}
              domain={[0, maxCalories]}
              tickCount={6}
              tickFormatter={(value) => `${value}`}
              label={{
                value: "ккал",
                angle: -90,
                position: "insideLeft",
                style: { fill: "var(--text-secondary)", fontSize: "11px" },
                offset: 10,
              }}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend
              wrapperStyle={{
                fontSize: "12px",
                color: "var(--text-secondary)",
                marginTop: "10px",
              }}
            />
            <Bar
              dataKey="proteins"
              stackId="a"
              fill="var(--macro-protein-color)"
              name="Белки"
              radius={[0, 0, 0, 0]}
            />
            <Bar
              dataKey="fats"
              stackId="a"
              fill="var(--warning-color)"
              name="Жиры"
              radius={[0, 0, 0, 0]}
            />
            <Bar
              dataKey="carbs"
              stackId="a"
              fill="var(--success-color)"
              name="Углеводы"
              radius={[8, 8, 0, 0]}
            />
          </BarChart>
        </ResponsiveContainer>
      </div>

      <div
        className="weekly-stats-summary-grid"
      >
        <StatCard
          value={`${avgCalories} ккал`}
          label="Среднее за неделю"
          color="var(--danger-color)"
        />
        <StatCard
          value={`${totalCalories} ккал`}
          label="Всего калорий"
          color="var(--primary-color)"
        />
        <StatCard
          value={streak}
          label="Дней подряд"
          color="var(--success-color)"
        />
      </div>
    </div>
  );
}