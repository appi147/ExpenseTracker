import { useEffect, useState } from "react";
import { getMonthlyInsight } from "@/services/expense-service";
import type { MonthlyExpenseInsight } from "@/services/expense-service";
import { Card, CardContent } from "@/components/ui/card";
import ToggleDuration from "./ToggleDuration";
import CategoryAccordion from "./CategoryAccordion";
import BudgetProgress from "./BudgetProgress";

type Props = {
  initialMonthly: boolean;
};

export default function ExpenseInsight({ initialMonthly }: Props) {
  const [monthly, setMonthly] = useState(initialMonthly);
  const [data, setData] = useState<MonthlyExpenseInsight | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchInsight = async () => {
      setLoading(true);
      const insight = await getMonthlyInsight(monthly);
      setData(insight);
      setLoading(false);
    };
    fetchInsight();
  }, [monthly]);

  return (
    <div className="max-w-2xl mx-auto p-4 space-y-4">
      <ToggleDuration monthly={monthly} onChange={setMonthly} />

      {loading && <p>Loading...</p>}
      {!loading && data && (
        <>
          <Card>
            <CardContent className="p-4">
              <BudgetProgress totalExpense={data.totalExpense} monthlyBudget={data.monthlyBudget} />
            </CardContent>
          </Card>

          <CategoryAccordion data={data.categoryWiseExpenses} />
        </>
      )}
    </div>
  );
}
