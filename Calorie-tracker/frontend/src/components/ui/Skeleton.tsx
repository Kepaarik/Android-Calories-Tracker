import React from "react";

interface SkeletonProps {
  variant?: "text" | "circle" | "rect" | "card";
  width?: string | number;
  height?: string | number;
  className?: string;
  style?: React.CSSProperties;
}

export default function Skeleton({
  variant = "text",
  width = "100%",
  height,
  className = "",
  style = {},
}: SkeletonProps) {
  const baseStyle: React.CSSProperties = {
    width,
    height: height || (variant === "text" ? "16px" : variant === "circle" ? "40px" : "100px"),
    borderRadius: variant === "circle" ? "50%" : variant === "card" ? "16px" : "8px",
    ...style,
  };

  return (
    <div
      className={`skeleton skeleton-${variant} ${className}`}
      style={baseStyle}
      aria-hidden="true"
    />
  );
}