import API from "./api";

export interface SubCategory {
  subCategoryId: number;
  label: string;
  deletable: boolean;
}

export const getAllSubCategories = async (
  categoryId: number
): Promise<SubCategory[]> => {
  const response = await API.get("/sub-category", {
    params: { categoryId },
  });
  return response.data;
};

export const createSubCategory = async (payload: {
  label: string;
  categoryId: number;
}): Promise<SubCategory> => {
  const response = await API.post("/sub-category/create", payload);
  return response.data;
};

export const updateSubCategory = async (
  id: number,
  payload: { label: string; }
): Promise<SubCategory> => {
  const response = await API.put(`/sub-category/${id}`, payload);
  return response.data;
};

export const deleteSubCategory = async (id: number): Promise<void> => {
  await API.delete(`/sub-category/${id}`);
};
