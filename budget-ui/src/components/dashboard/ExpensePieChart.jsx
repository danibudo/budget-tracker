import { Text, Loader, Stack, Title } from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { useFetch } from "../../hooks/useFetch";
import { formatEuro } from "../../utils/format";

const COLORS = ['#1971c2', '#e03131', '#2f9e44', '#f08c00', '#7048e8', '#099268', '#e64980', '#1098ad'];

const RADIAN = Math.PI / 180;

export default function ExpensePieChart({ refreshKey }) {
  const isMobile = useMediaQuery('(max-width: 768px)');

  function renderCustomizedLabel({ cx, cy, midAngle, outerRadius, percent, category }) {
    const radius = outerRadius * 1.3;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);
    return (
      <text
        x={x}
        y={y}
        textAnchor={x > cx ? 'start' : 'end'}
        dominantBaseline="central"
        fontSize={12}
      >
        {isMobile ? `${(percent * 100).toFixed(0)}%` : `${category} ${(percent * 100).toFixed(0)}%`}
      </text>
    );
  }
  const { data, loading, error } = useFetch('/summary/expenses-by-category', [refreshKey]);

  if (loading) return <Loader />;
  if (error) return <Text c="red">{error}</Text>;

  const items = (data ?? []).map(item => ({ ...item, amount: parseFloat(item.amount) }));

  if (items.length === 0) return <Text c="dimmed">No expense data to display.</Text>;

  return (
    <Stack gap="xs">
      <Title order={5}>Expenses by category</Title>
      <Text size="xs" c="dimmed">Total spent per expense category across all time</Text>
      <ResponsiveContainer width="100%" aspect={1.6}>
      <PieChart>
        <Pie
          data={items}
          dataKey="amount"
          nameKey="category"
          cy="44%"
          outerRadius="60%"
          label={renderCustomizedLabel}
          labelLine={!isMobile}
        >
          {items.map((_, i) => (
            <Cell key={i} fill={COLORS[i % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip formatter={(value) => formatEuro(value)} />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
    </Stack>
  );
}
