// frontend/src/store/selectedDateStore.ts
import { create } from "zustand";

const getTodayLocalDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

interface SelectedDateState {
  selectedDate: string;
  setSelectedDate: (date: string) => void;
}

export const useSelectedDateStore = create<SelectedDateState>((set) => ({
  selectedDate: getTodayLocalDate(),
  setSelectedDate: (date) => set({ selectedDate: date }),
}));
