import React from "react";

type ButtonVariant = "default" | "success" | "danger" | "icon";

interface GlassButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  icon?: React.ReactNode;
  fullWidth?: boolean;
  children?: React.ReactNode;
}

export default function GlassButton({
  variant = "default",
  icon,
  fullWidth = false,
  children,
  className = "",
  style = {},
  ...props
}: GlassButtonProps) {
  const variantClass = {
    default: "",
    success: "glass-btn-success",
    danger: "glass-btn-danger",
    icon: "btn-icon",
  }[variant];

  return (
    <button
      className={`glass-btn ${variantClass} ${className}`}
      style={{ width: fullWidth ? "100%" : undefined, ...style }}
      {...props}
    >
      {icon && <span className="btn-icon-wrapper">{icon}</span>}
      {children}
    </button>
  );
}