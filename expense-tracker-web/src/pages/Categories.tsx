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
import { Pencil, Settings } from "lucide-react";
import ManageSubcategoriesModal from "./ManageSubcategoriesModal";

const CategoriesPage = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [newCategoryLabel, setNewCategoryLabel] = useState("");
  const [editingCategoryId, setEditingCategoryId] = useState<number | null>(
    null
  );
  const [editingLabel, setEditingLabel] = useState("");
  const [activeSubCatCategory, setActiveSubCatCategory] =
    useState<Category | null>(null);

  const loadData = async () => {
    const cats = await getAllCategories();
    setCategories(cats);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateCategory = async () => {
    if (!newCategoryLabel.trim()) return;
    try {
      await createCategory({ label: newCategoryLabel });
      toast.success("Category created successfully.");
      setNewCategoryLabel("");
      loadData();
    } catch {
      toast.error("Failed to create category.");
    }
  };

  const handleEditSubmit = async (id: number) => {
    if (!editingLabel.trim()) return;
    try {
      await updateCategory(id, { label: editingLabel });
      toast.success("Category updated successfully.");
      setEditingCategoryId(null);
      loadData();
    } catch {
      toast.error("Failed to update category.");
    }
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
            <Button onClick={handleCreateCategory}>Add Category</Button>
          </div>

          <ul className="list-disc ml-5 space-y-2">
            {categories.map((cat) => (
              <li key={cat.categoryId} className="flex items-center gap-2">
                {editingCategoryId === cat.categoryId ? (
                  <>
                    <Input
                      value={editingLabel}
                      onChange={(e) => setEditingLabel(e.target.value)}
                      className="w-64"
                    />
                    <Button
                      size="sm"
                      onClick={() => handleEditSubmit(cat.categoryId)}
                    >
                      Save
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => setEditingCategoryId(null)}
                    >
                      Cancel
                    </Button>
                  </>
                ) : (
                  <>
                    <span className="font-medium">{cat.label}</span>
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => {
                              setEditingCategoryId(cat.categoryId);
                              setEditingLabel(cat.label);
                            }}
                            className="hover:bg-muted"
                          >
                            <Pencil className="w-4 h-4 text-muted-foreground" />
                          </Button>
                        </TooltipTrigger>
                        <TooltipContent>Edit category</TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
                  </>
                )}

                {cat.deletable ? (
                  <Button
                    variant="destructive"
                    size="sm"
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

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setActiveSubCatCategory(cat)}
                  className="ml-2 flex gap-1 items-center"
                >
                  <Settings className="w-4 h-4" /> Manage Subcategories
                </Button>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>

      {/* Subcategory Modal */}
      {activeSubCatCategory && (
        <ManageSubcategoriesModal
          category={activeSubCatCategory}
          onClose={() => setActiveSubCatCategory(null)}
          onRefresh={loadData}
        />
      )}
    </div>
  );
};

export default CategoriesPage;
