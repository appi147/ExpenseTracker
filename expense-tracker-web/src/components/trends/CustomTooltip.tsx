export const CustomTooltip = ({ active, payload, label }: any) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-muted text-foreground p-2 rounded shadow border text-sm">
        <div className="font-medium mb-1">{label}</div>
        {payload.map((entry: any, index: number) => (
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
