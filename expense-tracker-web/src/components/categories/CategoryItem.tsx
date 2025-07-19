import { useState } from "react";
import { AccordionItem, AccordionTrigger, AccordionContent } from "@/components/ui/accordion";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2, Plus, FolderIcon } from "lucide-react";
import { toast } from "sonner";
import { deleteCategory, updateCategory, type Category } from "@/services/category-service";
import {
  getAllSubCategories,
  createSubCategory,
  updateSubCategory,
  deleteSubCategory,
} from "@/services/sub-category-service";
import SubCategoryItem from "./SubCategoryItem";

interface SubCategory {
  subCategoryId: number;
  label: string;
  deletable: boolean;
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
      await createSubCategory({
        label: newSubLabel,
        categoryId: category.categoryId,
      });
      setNewSubLabel("");
      loadSubcategories();
      reloadCategories();
      toast.success("Subcategory created.");
    } catch {
      toast.error("Failed to create subcategory");
    }
  };

  const handleSubUpdate = async (subId: number, label: string) => {
    if (!label.trim()) return;
    try {
      await updateSubCategory(subId, { label });
      toast.success("Subcategory updated");
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
        className="text-xl font-semibold no-underline hover:no-underline"
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
              <Button size="sm" onClick={handleCategoryUpdate}>
                Save
              </Button>
              <Button variant="ghost" size="sm" onClick={() => setEditingCategoryId(null)}>
                Cancel
              </Button>
            </>
          ) : (
            <>
              <FolderIcon className="w-4 h-4" />
              <span>{category.label}</span>
              <Button
                variant="ghost"
                size="icon"
                className="h-2 w-2"
                onClick={() => {
                  setEditingCategoryId(category.categoryId);
                  setEditingLabel(category.label);
                }}
              >
                <Pencil className="w-2 h-2 text-muted-foreground" />
              </Button>
              {category.deletable && (
                <Trash2
                  className="w-2 h-2 text-destructive cursor-pointer"
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
            <SubCategoryItem
              key={sub.subCategoryId}
              sub={sub}
              categoryId={category.categoryId}
              onUpdate={(subId, label) => handleSubUpdate(subId, label)}
              onDelete={(subId) => handleSubDelete(subId)}
            />
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
