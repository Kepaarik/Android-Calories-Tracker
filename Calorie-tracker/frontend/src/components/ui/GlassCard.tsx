interface GlassCardProps {
  children: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
  onClick?: () => void;
  padding?: string;
}

export default function GlassCard({ children, className = "", style = {}, onClick, padding = "16px" }: GlassCardProps) {
  return (
    <div className={`glass card ${className}`} style={{ padding, ...style }} onClick={onClick}>
      {children}
    </div>
  );
}