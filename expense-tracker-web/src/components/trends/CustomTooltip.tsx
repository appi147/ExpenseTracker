import type { LegendPayload } from "recharts";

type TooltipPayload = LegendPayload & {
  stroke?: string;
  name?: string;
};

type CustomTooltipProps = {
  active?: boolean;
  payload?: readonly TooltipPayload[];
  label?: string;
};

export const CustomTooltip = ({ active, payload, label }: CustomTooltipProps) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-muted text-foreground p-2 rounded shadow border text-sm">
        <div className="font-medium mb-1">{label}</div>
        {payload.map((entry, index) => (
          <div key={`item-${index}`} className="flex justify-between">
            <span className="mr-2" style={{ color: entry.stroke }}>
              {entry.name}
            </span>
            <span>{entry.value}</span>
          </div>
        ))}
      </div>
    );
  }
  return null;
};
