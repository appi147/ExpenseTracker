import ExpenseInsight from "@/components/insights/ExpenseInsight";
import { useSearchParams } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { BarChart2 } from "lucide-react";

export default function MonthlyInsights() {
  const [searchParams] = useSearchParams();
  const monthlyParam = searchParams.get("monthly");
  const monthly = monthlyParam !== "false";

  return (
    <div className="p-6 max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-center gap-3">
        <BarChart2 className="w-6 h-6 text-primary" />
        <h1 className="text-3xl font-semibold text-center text-foreground">Expense Insights</h1>
      </div>

      <Card>
        <CardContent className="p-6">
          <ExpenseInsight initialMonthly={monthly} />
        </CardContent>
      </Card>
    </div>
  );
}
