import React, { useEffect, useState } from "react";
import { getInsight, type Insight } from "@/services/insight-service";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Loader2 } from "lucide-react";

const SiteWideInsights: React.FC = () => {
  const [data, setData] = useState<Insight | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getInsight()
      .then(setData)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const insights = [
    { label: "Users Registered", value: data?.totalUsersRegistered },
    { label: "Users Added Expenses", value: data?.totalUsersAddedExpense },
    {
      label: "Total Expenses",
      value: `₹${data?.totalExpensesAdded.toLocaleString()}`,
    },
    { label: "Transactions Added", value: data?.totalTransactionsAdded },
    { label: "Categories", value: data?.totalCategoriesCreated },
    { label: "Subcategories", value: data?.totalSubCategoriesCreated },
  ];

  return (
    <div className="p-6">
      <h2 className="text-2xl font-semibold mb-6">Site-wide Insights</h2>
      {loading ? (
        <div className="flex justify-center items-center h-40">
          <Loader2 className="animate-spin w-6 h-6 text-muted-foreground" />
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {insights.map(({ label, value }) => (
            <Card key={label} className="rounded-2xl shadow-sm">
              <CardHeader>
                <CardTitle className="text-base text-muted-foreground">{label}</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-2xl font-bold">{value}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default SiteWideInsights;
