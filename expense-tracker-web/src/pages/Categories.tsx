import { useEffect, useState } from "react";
import { getAllCategories, createCategory, type Category } from "@/services/category-service";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Accordion } from "@/components/ui/accordion";
import { toast } from "sonner";
import CategoryItem from "@/components/categories/CategoryItem";
import { Separator } from "@/components/ui/separator";

export default function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [newCategoryLabel, setNewCategoryLabel] = useState("");

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
      toast.success("Category created.");
      setNewCategoryLabel("");
      loadData();
    } catch {
      toast.error("Failed to create category");
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-4 space-y-6">
      <Card>
        <CardContent className="p-4 space-y-4">
          <h2 className="text-2xl font-bold text-center">Categories</h2>
          <Separator className="my-4" />
          <div className="flex gap-2">
            <Input
              value={newCategoryLabel}
              onChange={(e) => setNewCategoryLabel(e.target.value)}
              placeholder="New category label"
            />
            <Button onClick={handleCreateCategory}>Add Category</Button>
          </div>

          {categories.length > 0 ? (
            <Accordion type="single" collapsible>
              {categories.map((cat) => (
                <CategoryItem key={cat.categoryId} category={cat} reloadCategories={loadData} />
              ))}
            </Accordion>
          ) : (
            <div className="text-center text-muted-foreground py-12">
              <p className="text-lg">No categories yet.</p>
              <p className="text-sm">Add one using the input above.</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
