import { useAuthStore } from "../../store/authStore";
import { format } from "date-fns";
import { ru } from "date-fns/locale";
import "./Header.css";

export default function Header() {
  const { user } = useAuthStore();

  const displayName = user?.first_name || user?.username || "Дневник";
  const todayLabel = format(new Date(), "EEEE, d MMMM", { locale: ru });

  return (
    <header className="glass app-header">
      {/* Динамическое имя */}
      <h1 className="app-header-name">
        {displayName}
      </h1>
      <p className="app-header-date">
        {todayLabel}
      </p>
    </header>
  );
}