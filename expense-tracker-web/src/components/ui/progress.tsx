"use client";

import * as React from "react";
import {
  Root as ProgressRoot,
  Indicator as ProgressIndicator,
} from "@radix-ui/react-progress";

import { cn } from "@/lib/utils";

interface CustomProgressProps
  extends React.ComponentPropsWithoutRef<typeof ProgressRoot> {
  indicatorColor: string;
}

const Progress = React.forwardRef<
  React.ElementRef<typeof ProgressRoot>,
  CustomProgressProps
>(({ className, value, indicatorColor, ...props }, ref) => (
  <ProgressRoot
    ref={ref}
    className={cn(
      "relative h-4 w-full overflow-hidden rounded-full bg-secondary",
      className
    )}
    {...props}
  >
    <ProgressIndicator
      className={cn("h-full transition-all duration-500", indicatorColor)}
      style={{ width: `${value || 0}%` }}
    />
  </ProgressRoot>
));

Progress.displayName = "Progress";

export { Progress };
