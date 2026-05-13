import { Badge } from "@mantine/core";

export default function TypeBadge({ type }) {
  return (
    <Badge color={type === "INCOME" ? "green" : "red"} variant="light">
      {type}
    </Badge>
  );
}
