const PRETTY_COLORS = [
  "#6366F1", // Indigo
  "#8B5CF6", // Violet
  "#EC4899", // Pink
  "#F472B6", // Light Pink
  "#F59E0B", // Amber
  "#FBBF24", // Yellow
  "#10B981", // Emerald
  "#34D399", // Light Green
  "#3B82F6", // Blue
  "#60A5FA", // Sky Blue
  "#0EA5E9", // Cyan
  "#06B6D4", // Teal
  "#14B8A6", // Aqua
  "#22C55E", // Green
  "#84CC16", // Lime
  "#EAB308", // Mustard
  "#FB923C", // Orange
  "#F97316", // Deep Orange
  "#EF4444", // Red
  "#F87171", // Light Red
  "#A855F7", // Purple
  "#E879F9", // Lavender
  "#D946EF", // Magenta
  "#6EE7B7", // Mint
  "#94A3B8", // Slate
] as const;

export function createCategoryColorMap(categories: string[]): Record<string, string> {
  const colorMap: Record<string, string> = {};
  categories.forEach((cat, i) => {
    colorMap[cat] = PRETTY_COLORS[i % PRETTY_COLORS.length];
  });
  return colorMap;
}
