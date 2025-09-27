import API from "./api";

export interface RecurringExpense {
  recurringExpenseId: number;
  amount: number;
  dayOfMonth: number;
  comments?: string;
  paymentType: {
    label: string;
  };
  subCategory: {
    label: string;
    category: {
      label: string;
    };
  };
}

export interface CreateRecurringExpenseRequest {
  amount: number;
  dayOfMonth: number;
  comments?: string;
  subCategoryId: number;
  paymentTypeCode: string;
}

/**
 * Create a new recurring expense
 */
export const createRecurringExpense = async (
  data: CreateRecurringExpenseRequest,
): Promise<void> => {
  await API.post("/recurring-expense/create", data);
};

/**
 * Get all recurring expenses for the current user
 */
export const getRecurringExpenses = async (): Promise<RecurringExpense[]> => {
  const response = await API.get("/recurring-expense/list");
  return response.data;
};

/**
 * Delete a recurring expense by ID
 */
export const deleteRecurringExpense = async (id: number): Promise<void> => {
  await API.delete(`/recurring-expense/${id}`);
};
