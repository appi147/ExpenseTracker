import { useEffect, useState, useRef } from "react";
import { getMonthlyTrends, type MonthlyTrendRow } from "@/services/insight-service";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts";
import type { LegendPayload } from "recharts";
import { createCategoryColorMap } from "@/utils/colors";
import { CustomTooltip } from "@/components/trends/CustomTooltip";
import { Button } from "@/components/ui/button";
import { toPng } from "html-to-image";

type CustomLegendProps = {
  payload?: readonly LegendPayload[];
};

export default function MonthlyExpenseTrends() {
  const chartRef = useRef<HTMLDivElement>(null);
  const [data, setData] = useState<MonthlyTrendRow[]>([]);
  const [hiddenLines, setHiddenLines] = useState<Record<string, boolean>>({});

  useEffect(() => {
    getMonthlyTrends().then(setData);
  }, []);

  const allCategories = Array.from(
    new Set(data.flatMap((row) => Object.keys(row.categoryAmounts))),
  );

  const colorMap = createCategoryColorMap(allCategories);

  const chartData = data.map((row) => {
    const filled = allCategories.reduce(
      (acc, cat) => {
        acc[cat] = row.categoryAmounts[cat] ?? 0;
        return acc;
      },
      {} as Record<string, number>,
    );
    return {
      month: row.month,
      ...filled,
    };
  });

  const handleLegendClick = (e: { dataKey: string }) => {
    const { dataKey } = e;
    setHiddenLines((prev) => ({ ...prev, [dataKey]: !prev[dataKey] }));
  };

  const renderLegend = ({ payload }: CustomLegendProps) => {
    return (
      <div className="flex flex-wrap justify-center items-center gap-4 mt-2">
        {payload?.map((entry) => {
          const key = entry.value as string;
          const isHidden = hiddenLines[key];

          return (
            <span
              key={key}
              onClick={() => handleLegendClick({ dataKey: key })}
              className={`cursor-pointer text-sm flex items-center gap-1 px-2 py-1 rounded ${
                isHidden ? "line-through opacity-50" : "font-medium"
              }`}
            >
              <span
                className="inline-block w-3 h-3 rounded-full border"
                style={{ backgroundColor: entry.color }}
              />
              {key}
            </span>
          );
        })}
      </div>
    );
  };

  const exportAsPng = async () => {
    if (!chartRef.current) return;

    try {
      const dataUrl = await toPng(chartRef.current, {
        cacheBust: true,
        backgroundColor: document.documentElement.classList.contains("dark")
          ? "#0f0f0f"
          : "#ffffff",
      });

      const link = document.createElement("a");
      link.download = "monthly-expense-trends.png";
      link.href = dataUrl;
      link.click();
    } catch (err) {
      console.error("Export failed", err);
    }
  };

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Monthly Expense Trends</CardTitle>
        <Button variant="outline" size="sm" onClick={exportAsPng}>
          Export PNG
        </Button>
      </CardHeader>
      <CardContent>
        <div ref={chartRef}>
          <ResponsiveContainer width="100%" height={400}>
            <LineChart data={chartData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip content={<CustomTooltip />} cursor={{ stroke: "#ccc", strokeWidth: 1 }} />
              <Legend content={renderLegend} />
              {allCategories.map((cat) => (
                <Line
                  key={cat}
                  type="monotone"
                  strokeWidth={2}
                  dataKey={cat}
                  stroke={colorMap[cat]}
                  dot={{ r: 2 }}
                  activeDot={{ r: 5 }}
                  hide={hiddenLines[cat]}
                />
              ))}
            </LineChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
}
