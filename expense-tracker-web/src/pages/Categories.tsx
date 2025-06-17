import { useEffect, useState } from "react";
import {
  getAllCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  type Category,
} from "@/services/category-service";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { toast } from "sonner";

const CategoriesPage = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [newCategoryLabel, setNewCategoryLabel] = useState("");
  const [editCategoryId, setEditCategoryId] = useState<number | null>(null);

  const loadData = async () => {
    const cats = await getAllCategories();
    setCategories(cats);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateOrUpdateCategory = async () => {
    if (!newCategoryLabel.trim()) return;

    try {
      if (editCategoryId !== null) {
        await updateCategory(editCategoryId, { label: newCategoryLabel });
        toast.success("Category updated successfully.");
        setEditCategoryId(null);
      } else {
        await createCategory({ label: newCategoryLabel });
        toast.success("Category created successfully.");
      }
      setNewCategoryLabel("");
      loadData();
    } catch (error) {
      toast.error("Something went wrong while saving the category.");
    }
  };

  const handleEdit = (cat: Category) => {
    setNewCategoryLabel(cat.label);
    setEditCategoryId(cat.categoryId);
  };

  return (
    <div className="max-w-4xl mx-auto p-4 space-y-6">
      <Card>
        <CardContent className="p-4 space-y-4">
          <h2 className="text-xl font-bold">Categories</h2>

          <div className="flex gap-2">
            <Input
              value={newCategoryLabel}
              onChange={(e) => setNewCategoryLabel(e.target.value)}
              placeholder="New category label"
            />
            <Button
              onClick={handleCreateOrUpdateCategory}
              className="cursor-pointer"
            >
              {editCategoryId ? "Update" : "Add Category"}
            </Button>
          </div>

          <ul className="list-disc ml-5 space-y-2">
            {categories.map((cat) => (
              <li key={cat.categoryId} className="flex items-center gap-2">
                <span className="font-medium">{cat.label}</span>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleEdit(cat)}
                  className="cursor-pointer"
                >
                  Edit
                </Button>

                {cat.deletable ? (
                  <Button
                    variant="destructive"
                    size="sm"
                    className="cursor-pointer"
                    onClick={() =>
                      deleteCategory(cat.categoryId)
                        .then(() => {
                          toast.success("Category deleted successfully.");
                          loadData();
                        })
                        .catch(() => {
                          toast.error(
                            "Failed to delete category. It may be in use."
                          );
                        })
                    }
                  >
                    Delete
                  </Button>
                ) : (
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <div>
                          <Button
                            variant="destructive"
                            size="sm"
                            disabled
                            className="cursor-not-allowed"
                            title="Cannot delete: used in subcategories"
                          >
                            Delete
                          </Button>
                        </div>
                      </TooltipTrigger>
                      <TooltipContent>
                        Cannot delete: used in subcategories
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                )}
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
};

export default CategoriesPage;
