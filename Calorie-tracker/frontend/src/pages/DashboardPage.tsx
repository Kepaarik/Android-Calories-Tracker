import { useState, useEffect } from "react";
import { diaryApi } from "../api/endpoints";
import { DiaryEntry } from "../types/api";
import DateSlider from "../components/ui/DateSlider";
import CalendarModal from "../components/ui/CalendarModal";
import MealsSectionWidget from "../components/dashboard/MealsSectionWidget";
import Skeleton from "../components/ui/Skeleton";
import Icon from "../components/ui/Icon";
import GlassButton from "../components/ui/GlassButton";
import Modal from "../components/ui/Modal";
import Toast from "../components/ui/Toast";
import { useToast } from "../context/ToastContext";
import Header from "../components/layout/Header";
import { useSelectedDateStore } from "../store/selectedDateStore";
import "./DashboardPage.css";

export default function DashboardPage() {
  const toast = useToast();
  const { selectedDate, setSelectedDate } = useSelectedDateStore();

  const [deletedEntry, setDeletedEntry] = useState<DiaryEntry | null>(null);
  const [entries, setEntries] = useState<DiaryEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [deletingEntry, setDeletingEntry] = useState<DiaryEntry | null>(null);
  const handleDeleteClick = (entry: DiaryEntry) => {
    setDeletingEntry(entry);
  };

  const confirmDelete = async () => {
    if (!deletingEntry) return;
    try {
      await diaryApi.deleteEntry(deletingEntry.id);
      setDeletedEntry(deletingEntry);
      loadData();
      setDeletingEntry(null);
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка удаления");
      setDeletingEntry(null);
    }
  };

  useEffect(() => {
    loadData();
  }, [selectedDate]);

  useEffect(() => {
    const handleUpdate = () => loadData();
    window.addEventListener("diaryUpdated", handleUpdate);
    return () => window.removeEventListener("diaryUpdated", handleUpdate);
  }, [selectedDate]);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const entriesRes = await diaryApi.getEntries(selectedDate);
      setEntries(entriesRes.data);
    } catch (err) {
      console.error("Ошибка загрузки данных:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const getLocalDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

  const handleToday = () => {
    const today = getLocalDate();
    setSelectedDate(today);
  };

  const handleUndoDelete = async () => {
    if (!deletedEntry) return;

    const productId = deletedEntry.product_id || deletedEntry.product?.id;
    if (!productId) {
      console.error("Cannot restore: product_id is missing");
      setDeletedEntry(null);
      return;
    }

    try {
      await diaryApi.addEntry({
        product_id: productId,
        weight_grams: deletedEntry.weight_grams || 100,
        meal_type: deletedEntry.meal_type || "breakfast",
        date: selectedDate,
      });
      loadData();
      setDeletedEntry(null);
    } catch (err: any) {
      console.error("Ошибка восстановления:", err);
      console.error("Details:", err.response?.data);
      loadData();
      setDeletedEntry(null);
    }
  };

  const handleEntryUpdated = () => {
    loadData();
  };

  if (isLoading) {
    return (
      <div className="container dashboard-page-container">
        <Skeleton
          variant="rect"
          height="56px"
          style={{ marginBottom: "16px", borderRadius: "16px" }}
        />
        <Skeleton
          variant="text"
          width="40%"
          height="24px"
          style={{ marginBottom: "12px" }}
        />
        <Skeleton
          variant="card"
          height="120px"
          style={{ marginBottom: "16px" }}
        />
        <Skeleton
          variant="card"
          height="120px"
          style={{ marginBottom: "16px" }}
        />
      </div>
    );
  }

  return (
    <div className="container dashboard-page-container">
      {/* Header */}
      <Header />
      {/* Кнопки управления датой */}
      <div className="dashboard-page-date-actions">
        <GlassButton
          onClick={handleToday}
          className="dashboard-page-date-action-btn"
        >
          <Icon name="clock" size={16} />
          Сегодня
        </GlassButton>
        <GlassButton
          onClick={() => setIsCalendarOpen(true)}
          className="dashboard-page-date-action-btn"
        >
          <Icon name="calendar" size={16} />
          Календарь
        </GlassButton>
      </div>

      <DateSlider selectedDate={selectedDate} onDateChange={setSelectedDate} />

      <MealsSectionWidget
        entries={entries}
        isLoading={false}
        onDelete={handleDeleteClick}
        onUpdated={handleEntryUpdated}
      />

      {deletedEntry && (
        <Toast
          message="Запись удалена"
          subtitle={deletedEntry.product?.name}
          onUndo={handleUndoDelete}
          onClose={() => setDeletedEntry(null)}
        />
      )}

      {/* Модальные окна */}
      <CalendarModal
        isOpen={isCalendarOpen}
        onClose={() => setIsCalendarOpen(false)}
        selectedDate={selectedDate}
        onDateSelect={(date) => {
          setSelectedDate(date);
          setIsCalendarOpen(false);
        }}
      />

      <Modal
        isOpen={!!deletingEntry}
        onClose={() => setDeletingEntry(null)}
        title="Подтвердите действие"
      >
        <p className="dashboard-page-modal-text">
          Удалить запись <strong>"{deletingEntry?.product?.name}"</strong>?
        </p>
        <div className="dashboard-page-modal-actions">
          <GlassButton onClick={() => setDeletingEntry(null)} fullWidth>
            Отмена
          </GlassButton>
          <GlassButton variant="danger" onClick={confirmDelete} fullWidth>
            Удалить
          </GlassButton>
        </div>
      </Modal>
    </div>
  );
}
