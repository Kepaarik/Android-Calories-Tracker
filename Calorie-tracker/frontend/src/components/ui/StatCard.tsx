import "./StatCard.css";

interface StatCardProps {
  value: string | number;
  label: string;
  color?: string;
  subtitle?: string;
}

export default function StatCard({ value, label, color, subtitle }: StatCardProps) {
  return (
    <div className="glass stat-card">
      <div className="stat-value" style={color ? { color } : undefined}>
        {value}
      </div>
      <div className="stat-label">{label}</div>
      {subtitle && (
        <div className="stat-card-subtitle">{subtitle}</div>
      )}
    </div>
  );
}