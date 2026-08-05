import { useLocation, useNavigate } from "react-router-dom";
import Icon from "../ui/Icon";

interface BottomNavProps {
  onAddClick: () => void;
}

const NAV_ITEMS = [
  { path: "/", icon: "home", label: "Главная" },
  { path: "/products", icon: "products", label: "Продукты" },
  { path: "/statistics", icon: "stats", label: "Статистика" },
  { path: "/profile", icon: "user", label: "Профиль" },
];

export default function BottomNav({ onAddClick }: BottomNavProps) {
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path: string) =>
    path === "/" ? location.pathname === "/" : location.pathname.startsWith(path);

  const [left, right] = [NAV_ITEMS.slice(0, 2), NAV_ITEMS.slice(2)];

  const renderItem = (item: (typeof NAV_ITEMS)[number]) => (
    <button
      key={item.path}
      onClick={() => navigate(item.path)}
      className="bottom-nav-item"
      data-active={isActive(item.path)}
      title={item.label}
    >
      <Icon name={item.icon} size={22} />
      <span>{item.label}</span>
    </button>
  );

  return (
    <nav className="bottom-nav glass">
      {left.map(renderItem)}

      <div className="bottom-nav-fab-slot">
        <button
          onClick={onAddClick}
          className="bottom-nav-fab"
          title="Добавить приём пищи"
        >
          <Icon name="plus" size={26} color="#fff" strokeWidth={2.5} />
        </button>
      </div>

      {right.map(renderItem)}
    </nav>
  );
}
