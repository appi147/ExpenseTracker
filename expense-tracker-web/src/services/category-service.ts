import API from "./api";

export interface Category {
  categoryId: number;
  label: string;
  deletable: boolean;
}

export const getAllCategories = async (): Promise<Category[]> => {
  const response = await API.get("/category");
  return response.data;
};

export const createCategory = async (payload: {
  label: string;
}): Promise<Category> => {
  const response = await API.post("/category/create", payload);
  return response.data;
};

export const updateCategory = async (
  categoryId: number,
  payload: { label: string }
): Promise<Category> => {
  const response = await API.put(`/category/${categoryId}`, payload);
  return response.data;
};

export const deleteCategory = async (categoryId: number): Promise<void> => {
  await API.delete(`/category/${categoryId}`);
};
