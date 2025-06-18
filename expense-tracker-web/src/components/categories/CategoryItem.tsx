import { useState } from "react";
import {
  AccordionItem,
  AccordionTrigger,
  AccordionContent,
} from "@/components/ui/accordion";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2, Plus } from "lucide-react";
import { toast } from "sonner";
import {
  deleteCategory,
  updateCategory,
  type Category,
} from "@/services/category-service";
import {
  getAllSubCategories,
  createSubCategory,
  updateSubCategory,
  deleteSubCategory,
} from "@/services/sub-category-service";

interface SubCategory {
  subCategoryId: number;
  label: string;
}

interface Props {
  category: Category;
  reloadCategories: () => void;
}

export default function CategoryItem({ category, reloadCategories }: Props) {
  const [editingCategoryId, setEditingCategoryId] = useState<number | null>(null);
  const [editingLabel, setEditingLabel] = useState(category.label);
  const [subcategories, setSubcategories] = useState<SubCategory[]>([]);
  const [newSubLabel, setNewSubLabel] = useState("");
  const [editingSubId, setEditingSubId] = useState<number | null>(null);
  const [editingSubLabel, setEditingSubLabel] = useState("");
  const [open, setOpen] = useState(false);

  const loadSubcategories = async () => {
    try {
      const data = await getAllSubCategories(category.categoryId);
      setSubcategories(data);
    } catch {
      toast.error("Failed to load subcategories");
    }
  };

  const handleCategoryUpdate = async () => {
    if (!editingLabel.trim()) return;
    try {
      await updateCategory(category.categoryId, { label: editingLabel });
      toast.success("Category updated.");
      setEditingCategoryId(null);
      reloadCategories();
    } catch {
      toast.error("Failed to update category");
    }
  };

  const handleCategoryDelete = async () => {
    try {
      await deleteCategory(category.categoryId);
      toast.success("Deleted category");
      reloadCategories();
    } catch {
      toast.error("Failed to delete category");
    }
  };

  const handleSubCreate = async () => {
    if (!newSubLabel.trim()) return;
    try {
      await createSubCategory({ label: newSubLabel, categoryId: category.categoryId });
      setNewSubLabel("");
      loadSubcategories();
      reloadCategories();
      toast.success("Subcategory created.");
    } catch {
      toast.error("Failed to create subcategory");
    }
  };

  const handleSubUpdate = async () => {
    if (!editingSubLabel.trim() || editingSubId === null) return;
    try {
      await updateSubCategory(editingSubId, { label: editingSubLabel });
      toast.success("Subcategory updated");
      setEditingSubId(null);
      setEditingSubLabel("");
      loadSubcategories();
    } catch {
      toast.error("Failed to update subcategory");
    }
  };

  const handleSubDelete = async (subId: number) => {
    try {
      await deleteSubCategory(subId);
      toast.success("Deleted subcategory");
      loadSubcategories();
      reloadCategories();
    } catch {
      toast.error("Failed to delete subcategory");
    }
  };

  return (
    <AccordionItem value={String(category.categoryId)}>
      <AccordionTrigger
        onClick={() => {
          setOpen(!open);
          if (!open) loadSubcategories();
        }}
      >
        <div className="flex items-center gap-2">
          {editingCategoryId === category.categoryId ? (
            <>
              <Input
                value={editingLabel}
                onChange={(e) => setEditingLabel(e.target.value)}
                className="w-64"
              />
              <Button size="sm" onClick={handleCategoryUpdate}>Save</Button>
              <Button variant="ghost" size="sm" onClick={() => setEditingCategoryId(null)}>
                Cancel
              </Button>
            </>
          ) : (
            <>
              <span className="font-medium">{category.label}</span>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => {
                  setEditingCategoryId(category.categoryId);
                  setEditingLabel(category.label);
                }}
              >
                <Pencil className="w-4 h-4 text-muted-foreground" />
              </Button>
              {category.deletable && (
                <Trash2
                  className="w-4 h-4 text-destructive cursor-pointer"
                  onClick={handleCategoryDelete}
                />
              )}
            </>
          )}
        </div>
      </AccordionTrigger>
      <AccordionContent>
        <div className="space-y-2">
          {subcategories.map((sub) => (
            <div key={sub.subCategoryId} className="flex items-center justify-between">
              {editingSubId === sub.subCategoryId ? (
                <>
                  <Input
                    value={editingSubLabel}
                    onChange={(e) => setEditingSubLabel(e.target.value)}
                    className="flex-1"
                  />
                  <Button size="sm" onClick={handleSubUpdate}>Save</Button>
                  <Button variant="ghost" size="sm" onClick={() => setEditingSubId(null)}>
                    Cancel
                  </Button>
                </>
              ) : (
                <>
                  <span>{sub.label}</span>
                  <div className="flex gap-2 items-center">
                    <Pencil
                      className="w-4 h-4 cursor-pointer"
                      onClick={() => {
                        setEditingSubId(sub.subCategoryId);
                        setEditingSubLabel(sub.label);
                      }}
                    />
                    <Trash2
                      className="w-4 h-4 text-destructive cursor-pointer"
                      onClick={() => handleSubDelete(sub.subCategoryId)}
                    />
                  </div>
                </>
              )}
            </div>
          ))}

          <div className="flex gap-2 mt-2">
            <Input
              value={newSubLabel}
              onChange={(e) => setNewSubLabel(e.target.value)}
              placeholder="New subcategory"
            />
            <Button size="sm" onClick={handleSubCreate}>
              <Plus className="w-4 h-4 mr-1" /> Add
            </Button>
          </div>
        </div>
      </AccordionContent>
    </AccordionItem>
  );
}
