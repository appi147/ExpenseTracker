import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { FileIcon, Pencil, Trash2 } from "lucide-react";

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
    <div className="flex items-center justify-between ml-4 pl-2 border-l border-gray-700 py-1">
      {isEditing ? (
        <div className="flex items-center gap-2 w-full">
          <Input
            value={editLabel}
            onChange={(e) => setEditLabel(e.target.value)}
            className="flex-1"
          />
          <Button size="sm" onClick={handleSave}>
            Save
          </Button>
          <Button variant="ghost" size="sm" onClick={() => setIsEditing(false)}>
            Cancel
          </Button>
        </div>
      ) : (
        <>
          <div className="flex items-center gap-2 text-sm overflow-hidden">
            <FileIcon className="w-4 h-4 shrink-0" />
            <span className="truncate">{sub.label}</span>
            <Pencil
              className="w-4 h-4 cursor-pointer text-muted-foreground shrink-0"
              onClick={() => setIsEditing(true)}
            />
          </div>

          {sub.deletable ? (
            <Trash2
              className="w-4 h-4 text-destructive cursor-pointer ml-2 shrink-0"
              onClick={() => onDelete(sub.subCategoryId)}
            />
          ) : (
            <Trash2 className="w-4 h-4 text-muted cursor-not-allowed ml-2 shrink-0" />
          )}
        </>
      )}
    </div>
  );
}
