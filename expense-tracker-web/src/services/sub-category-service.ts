import API from "./api";

export const getAllSubCategories = async () => {
  const response = await API.get("/sub-category");
  return response.data;
};

export const createSubCategory = async (payload: { label: string; categoryId: number }) => {
  const response = await API.post("/sub-category/create", payload);
  return response.data;
};

export const updateSubCategory = async (id: number, payload: { label: string; categoryId: number }) => {
  const response = await API.put(`/sub-category/${id}`, payload);
  return response.data;
};

export const deleteSubCategory = async (id: number) => {
  await API.delete(`/sub-category/${id}`);
};
