import React, { useEffect, useState } from "react";
import {
  getAllCategories,
  createCategory,
  updateCategory,
  deleteCategory,
} from "@/services/category-service";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

const CategoriesPage = () => {
  const [categories, setCategories] = useState<any[]>([]);
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
    if (editCategoryId !== null) {
      await updateCategory(editCategoryId, { label: newCategoryLabel });
      setEditCategoryId(null);
    } else {
      await createCategory({ label: newCategoryLabel });
    }
    setNewCategoryLabel("");
    loadData();
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
            <Button onClick={handleCreateOrUpdateCategory}>
              {editCategoryId ? "Update" : "Add Category"}
            </Button>
          </div>
          <ul className="list-disc ml-5 space-y-1">
            {categories.map((cat) => (
              <li key={cat.categoryId} className="flex items-center gap-2">
                <span className="font-medium">{cat.label}</span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => {
                    setNewCategoryLabel(cat.label);
                    setEditCategoryId(cat.categoryId);
                  }}
                >
                  Edit
                </Button>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => deleteCategory(cat.categoryId).then(loadData)}
                >
                  Delete
                </Button>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
};

export default CategoriesPage;
