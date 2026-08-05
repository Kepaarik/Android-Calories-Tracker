import GlassCard from "../../components/ui/GlassCard";
import StatCard from "../../components/ui/StatCard";
import "./ProfilePreview.css";

interface ProfilePreviewProps {
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
}

export default function ProfilePreview({ calories, proteins, fats, carbs }: ProfilePreviewProps) {
  return (
    <GlassCard className="profile-preview-card" padding="20px">
      <h3 className="profile-preview-title">
        Ваша суточная норма
      </h3>
      <div className="stats-grid">
        <StatCard value={calories} label="ккал" color="var(--danger-color)" />
        <StatCard value={proteins} label="белки (г)" color="var(--macro-protein-color)" />
        <StatCard value={fats} label="жиры (г)" color="var(--warning-color)" />
        <StatCard value={carbs} label="углеводы (г)" color="var(--success-color)" />
      </div>
    </GlassCard>
  );
}