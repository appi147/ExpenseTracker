import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import {
  getAllSubCategories,
  createSubCategory,
  updateSubCategory,
  deleteSubCategory,
} from "@/services/sub-category-service";

import type { Category } from "@/services/category-service";

type SubCategory = {
  subCategoryId: number;
  label: string;
  categoryId: number;
};

type Props = {
  category: Category;
  onClose: () => void;
  onRefresh: () => void;
};

const ManageSubcategoriesModal = ({ category, onClose, onRefresh }: Props) => {
  const [subcategories, setSubcategories] = useState<SubCategory[]>([]);
  const [newLabel, setNewLabel] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editingLabel, setEditingLabel] = useState("");

  const loadSubcategories = async () => {
    try {
      const all = await getAllSubCategories();
      const filtered = all.filter(
        (sc: SubCategory) => sc.categoryId === category.categoryId
      );
      setSubcategories(filtered);
    } catch {
      toast.error("Failed to load subcategories.");
    }
  };

  useEffect(() => {
    loadSubcategories();
  }, [category]);

  const handleCreate = async () => {
    if (!newLabel.trim()) return;
    try {
      await createSubCategory({
        label: newLabel,
        categoryId: category.categoryId,
      });
      toast.success("Subcategory created.");
      setNewLabel("");
      loadSubcategories();
      onRefresh();
    } catch {
      toast.error("Failed to create subcategory.");
    }
  };

  const handleEdit = async (id: number) => {
    if (!editingLabel.trim()) return;
    try {
      await updateSubCategory(id, {
        label: editingLabel,
        categoryId: category.categoryId,
      });
      toast.success("Subcategory updated.");
      setEditingId(null);
      loadSubcategories();
      onRefresh();
    } catch {
      toast.error("Failed to update subcategory.");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteSubCategory(id);
      toast.success("Subcategory deleted.");
      loadSubcategories();
      onRefresh();
    } catch {
      toast.error("Failed to delete subcategory.");
    }
  };

  return (
    <Dialog open onOpenChange={onClose}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Manage Subcategories for {category.label}</DialogTitle>
        </DialogHeader>

        <div className="space-y-4 max-h-80 overflow-y-auto pr-2">
          {subcategories.map((sub) => (
            <div key={sub.subCategoryId} className="flex gap-2 items-center">
              {editingId === sub.subCategoryId ? (
                <>
                  <Input
                    value={editingLabel}
                    onChange={(e) => setEditingLabel(e.target.value)}
                  />
                  <Button
                    size="sm"
                    onClick={() => handleEdit(sub.subCategoryId)}
                  >
                    Save
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setEditingId(null)}
                  >
                    Cancel
                  </Button>
                </>
              ) : (
                <>
                  <span className="font-medium">{sub.label}</span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      setEditingId(sub.subCategoryId);
                      setEditingLabel(sub.label);
                    }}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => handleDelete(sub.subCategoryId)}
                  >
                    Delete
                  </Button>
                </>
              )}
            </div>
          ))}
        </div>

        <div className="flex gap-2 mt-4">
          <Input
            value={newLabel}
            onChange={(e) => setNewLabel(e.target.value)}
            placeholder="New subcategory"
          />
          <Button onClick={handleCreate}>Add</Button>
        </div>

        <DialogFooter>
          <Button variant="secondary" onClick={onClose}>
            Close
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ManageSubcategoriesModal;
