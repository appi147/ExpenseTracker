import { Root as LabelPrimitiveRoot } from "@radix-ui/react-label";
import { cn } from "@/lib/utils";

interface LabelProps extends Readonly<React.ComponentProps<typeof LabelPrimitiveRoot>> {
  readonly variant?: "inline" | "stacked";
}

const Label = ({ className, variant = "stacked", ...props }: LabelProps) => {
  return (
    <LabelPrimitiveRoot
      data-slot="label"
      className={cn(
        "text-sm leading-none font-medium select-none group-data-[disabled=true]:pointer-events-none group-data-[disabled=true]:opacity-50 peer-disabled:cursor-not-allowed peer-disabled:opacity-50",
        variant === "inline" && "flex items-center gap-2",
        variant === "stacked" && "block mb-1",
        className,
      )}
      {...props}
    />
  );
};

export { Label };
