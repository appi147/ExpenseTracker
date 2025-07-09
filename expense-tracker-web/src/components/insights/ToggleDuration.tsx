import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";

type Props = {
  monthly: boolean;
  onChange: (val: boolean) => void;
};

export default function ToggleDuration({ monthly, onChange }: Props) {
  return (
    <div className="flex items-center gap-2 mb-4">
      <Label htmlFor="toggle-duration">Last 30 Days</Label>
      <Switch
        id="toggle-duration"
        className="cursor-pointer"
        checked={monthly}
        onCheckedChange={onChange}
      />
      <Label htmlFor="toggle-duration">Current Month</Label>
    </div>
  );
}
