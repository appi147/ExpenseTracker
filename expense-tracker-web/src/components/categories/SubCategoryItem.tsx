import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2 } from "lucide-react";

interface SubCategoryItemProps {
  sub: {
    subCategoryId: number;
    label: string;
    deletable: boolean;
  };
  categoryId: number;
  onUpdate: (subId: number, label: string) => void;
  onDelete: (subId: number) => void;
}

export default function SubCategoryItem({ sub, onUpdate, onDelete }: SubCategoryItemProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editLabel, setEditLabel] = useState(sub.label);

  const handleSave = () => {
    if (!editLabel.trim()) return;
    onUpdate(sub.subCategoryId, editLabel);
    setIsEditing(false);
  };

  return (
    <div className="flex items-center justify-between">
      {isEditing ? (
        <>
          <Input
            value={editLabel}
            onChange={(e) => setEditLabel(e.target.value)}
            className="flex-1"
          />
          <Button size="sm" onClick={handleSave}>Save</Button>
          <Button variant="ghost" size="sm" onClick={() => setIsEditing(false)}>Cancel</Button>
        </>
      ) : (
        <>
          <span>{sub.label}</span>
          <div className="flex gap-2 items-center">
            <Pencil
              className="w-4 h-4 cursor-pointer"
              onClick={() => setIsEditing(true)}
            />
            {sub.deletable ? (
              <Trash2
                className="w-4 h-4 text-destructive cursor-pointer"
                onClick={() => onDelete(sub.subCategoryId)}
              />
            ) : (
              <Trash2
                className="w-4 h-4 text-muted cursor-not-allowed"
              />
            )}
          </div>
        </>
      )}
    </div>
  );
}
