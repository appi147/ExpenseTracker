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

export const createCategory = async (payload: { label: string }): Promise<Category> => {
  const response = await API.post("/category/create", payload);
  return response.data;
};

export const updateCategory = async (id: number, payload: { label: string }): Promise<Category> => {
  const response = await API.put(`/category/${id}`, payload);
  return response.data;
};

export const deleteCategory = async (id: number): Promise<void> => {
  await API.delete(`/category/${id}`);
};
