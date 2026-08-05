import { useState, useEffect, useRef, ReactNode } from "react";
import { createPortal } from "react-dom";
import "./DropdownPortal.css";

interface DropdownPortalProps {
  children: ReactNode;
  isOpen: boolean;
  anchorRef: React.RefObject<HTMLElement>;
  maxHeight?: string;
}

export default function DropdownPortal({
  children,
  isOpen,
  anchorRef,
  maxHeight = "250px",
}: DropdownPortalProps) {
  const [style, setStyle] = useState<React.CSSProperties>({});

  useEffect(() => {
    if (!isOpen || !anchorRef.current) return;

    const updatePosition = () => {
      if (!anchorRef.current) return;
      const rect = anchorRef.current.getBoundingClientRect();

      setStyle({
        position: "fixed",
        top: `${rect.bottom + 4}px`,
        left: `${rect.left}px`,
        width: `${rect.width}px`,
        zIndex: 11000, // Выше модалки (у модалки 10000)
        maxHeight,
        overflowY: "auto",
      });
    };

    updatePosition();

    window.addEventListener("scroll", updatePosition, true);
    window.addEventListener("resize", updatePosition);

    return () => {
      window.removeEventListener("scroll", updatePosition, true);
      window.removeEventListener("resize", updatePosition);
    };
  }, [isOpen, anchorRef, maxHeight, children]);

  if (!isOpen) return null;

  return createPortal(
    <div className="glass card dropdown-portal" style={style}>
      {children}
    </div>,
    document.body
  );
}
