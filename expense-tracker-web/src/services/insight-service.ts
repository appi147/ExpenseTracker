import API from "./api";

export interface Insight {
  totalUsersRegistered: number;
  totalUsersAddedExpense: number;
  totalExpensesAdded: number;
  totalTransactionsAdded: number;
  totalCategoriesCreated: number;
  totalSubCategoriesCreated: number;
}

export interface MonthlyTrendRow {
  month: string;
  categoryAmounts: Record<string, number>;
}

export const getInsight = async (): Promise<Insight> => {
  const response = await API.get("/insights/site-wide");
  return response.data;
};

export const getMonthlyTrends = async (): Promise<MonthlyTrendRow[]> => {
  const response = await API.get("/insights/monthly-trends");
  return response.data;
};
