import GlassButton from "./GlassButton";
import GlassCard from "./GlassCard";
import Icon from "./Icon";
import "./PageHeader.css";

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  onBack?: () => void;
  actions?: React.ReactNode;
}

export default function PageHeader({ title, subtitle, onBack, actions }: PageHeaderProps) {
  return (
    <GlassCard className="page-header-card" padding="12px 16px">
      <div className="page-header-row">
        {onBack && (
          <GlassButton variant="icon" onClick={onBack}>
            <Icon name="back" size={20} />
          </GlassButton>
        )}
        <div className="page-header-text">
          <h1 className="page-header-title">{title}</h1>
          {subtitle && (
            <p className="page-header-subtitle">{subtitle}</p>
          )}
        </div>
        {actions && <div className="page-header-actions">{actions}</div>}
      </div>
    </GlassCard>
  );
}