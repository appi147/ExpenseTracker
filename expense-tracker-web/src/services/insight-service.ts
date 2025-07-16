import API from "./api";

export interface Insight {
  totalUsersRegistered: number;
  totalUsersAddedExpense: number;
  totalExpensesAdded: number;
  totalTransactionsAdded: number;
  totalCategoriesCreated: number;
  totalSubCategoriesCreated: number;
}

export const getInsight = async (): Promise<Insight> => {
  const response = await API.get("/insights/site-wide");
  return response.data;
};
