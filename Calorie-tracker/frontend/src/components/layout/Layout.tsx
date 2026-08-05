import { useState } from "react";
import BottomNav from "./BottomNav";
import AddEntryModal from "../diary/AddEntryModal";

interface LayoutProps {
  children: React.ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const [isAddEntryOpen, setIsAddEntryOpen] = useState(false);

  return (
    <>
      <div className="layout-content">{children}</div>

      <BottomNav onAddClick={() => setIsAddEntryOpen(true)} />

      <AddEntryModal
        isOpen={isAddEntryOpen}
        onClose={() => setIsAddEntryOpen(false)}
        onAdded={() => setIsAddEntryOpen(false)}
      />
    </>
  );
}
