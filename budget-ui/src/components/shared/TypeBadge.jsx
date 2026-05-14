import { Badge } from "@mantine/core";

export default function TypeBadge({ type }) {
  const color = type === "INCOME" ? "green" : "red";
  return (
    <>
      <Badge color={color} variant="light" visibleFrom="sm">{type}</Badge>
      <Badge color={color} variant="light" hiddenFrom="sm" styles={{ label: { overflow: 'visible' } }}>{type.slice(0, 3)}</Badge>
    </>
  );
}
