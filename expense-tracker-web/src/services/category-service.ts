import API from "./api";

export const getAllCategories = async () => {
  const response = await API.get("/category");
  return response.data;
};

export const createCategory = async (payload: { label: string }) => {
  const response = await API.post("/category/create", payload);
  return response.data;
};

export const updateCategory = async (id: number, payload: { label: string }) => {
  const response = await API.put(`/category/${id}`, payload);
  return response.data;
};

export const deleteCategory = async (id: number) => {
  await API.delete(`/category/${id}`);
};
