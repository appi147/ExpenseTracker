import { useEffect, useState, useMemo } from "react";
import { getMonthlyInsight } from "@/services/expense-service";
import type { MonthlyExpenseInsight } from "@/services/expense-service";
import { Card, CardContent } from "@/components/ui/card";
import ToggleDuration from "./ToggleDuration";
import CategoryAccordion from "./CategoryAccordion";
import BudgetProgress from "./BudgetProgress";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { createCategoryColorMap } from "@/utils/colors";
import { Button } from "@/components/ui/button";
import { ChevronDown, ChevronRight } from "lucide-react";

type Props = {
  initialMonthly: boolean;
};

export default function ExpenseInsight({ initialMonthly }: Props) {
  const [monthly, setMonthly] = useState(initialMonthly);
  const [data, setData] = useState<MonthlyExpenseInsight | null>(null);
  const [loading, setLoading] = useState(false);
  const [showPieChart, setShowPieChart] = useState(false);

  useEffect(() => {
    const fetchInsight = async () => {
      setLoading(true);
      const insight = await getMonthlyInsight(monthly);
      setData(insight);
      setLoading(false);
    };
    fetchInsight();
  }, [monthly]);

  const pieChartData = useMemo(
    () =>
      data?.categoryWiseExpenses.map((item) => ({
        name: item.category,
        value: item.amount,
      })) ?? [],
    [data],
  );

  const colorMap = useMemo(() => {
    return createCategoryColorMap(pieChartData.map((d) => d.name));
  }, [pieChartData]);

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

          <Card>
            <CardContent className="p-4 space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold">Category-wise Spending</h2>
                <Button variant="ghost" size="sm" onClick={() => setShowPieChart((prev) => !prev)}>
                  {showPieChart ? (
                    <>
                      <ChevronDown className="w-4 h-4 mr-1" />
                      Hide Pie Chart
                    </>
                  ) : (
                    <>
                      <ChevronRight className="w-4 h-4 mr-1" />
                      Show Pie Chart
                    </>
                  )}
                </Button>
              </div>

              {showPieChart && (
                <div className="transition-all duration-300 ease-in-out">
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={pieChartData}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        label
                      >
                        {pieChartData.map((entry) => (
                          <Cell key={entry.name} fill={colorMap[entry.name]} />
                        ))}
                      </Pie>
                      <Tooltip />
                      <Legend layout="vertical" verticalAlign="middle" align="right" />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              )}
            </CardContent>
          </Card>

          <CategoryAccordion data={data.categoryWiseExpenses} />
        </>
      )}
    </div>
  );
}
