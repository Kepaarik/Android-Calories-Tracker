// frontend/src/components/icons/WidgetIcons.tsx
interface WidgetIconProps {
  widgetId: string;
  size?: number;
  color?: string;
}
export const WidgetIcon: React.FC<WidgetIconProps> = ({
  widgetId,
  size = 20,
  color = "currentColor",
}) => {
  const props = {
    width: size,
    height: size,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: color,
    strokeWidth: 2,
    strokeLinecap: "round" as const,
    strokeLinejoin: "round" as const,
  };

  switch (widgetId) {
    case "summary":
      return (
        <svg {...props}>
          <path d="M18 20V10"></path>
          <path d="M12 20V4"></path>
          <path d="M6 20v-6"></path>
        </svg>
      );
    case "water":
      return (
        <svg {...props}>
          <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"></path>
        </svg>
      );
    case "weekly_stats":
      return (
        <svg {...props}>
          <line x1="18" y1="20" x2="18" y2="10"></line>
          <line x1="12" y1="20" x2="12" y2="4"></line>
          <line x1="6" y1="20" x2="6" y2="14"></line>
        </svg>
      );
    case "weight":
      return (
        <svg {...props}>
          <path d="M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2z"></path>
          <path d="M12 6v6l4 2"></path>
        </svg>
      );
    default:
      return null;
  }
};
