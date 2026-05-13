import { Text, Loader } from "@mantine/core";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { useFetch } from "../../hooks/useFetch";
import { formatEuro } from "../../utils/format";

const COLORS = ['#1971c2', '#e03131', '#2f9e44', '#f08c00', '#7048e8', '#099268', '#e64980', '#1098ad'];

const RADIAN = Math.PI / 180;

function renderCustomizedLabel({ cx, cy, midAngle, outerRadius, percent, category }) {
  const radius = outerRadius + 32;
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
      {`${category} ${(percent * 100).toFixed(0)}%`}
    </text>
  );
}

export default function ExpensePieChart({ refreshKey }) {
  const { data, loading, error } = useFetch('/summary/expenses-by-category', [refreshKey]);

  if (loading) return <Loader />;
  if (error) return <Text c="red">{error}</Text>;

  const items = (data ?? []).map(item => ({ ...item, amount: parseFloat(item.amount) }));

  if (items.length === 0) return <Text c="dimmed">No expense data to display.</Text>;

  return (
    <ResponsiveContainer width="100%" aspect={2}>
      <PieChart title="Total expenses by category" >
        <Pie
          data={items}
          dataKey="amount"
          nameKey="category"
          outerRadius={120}
          label={renderCustomizedLabel}
          labelLine
        >
          {items.map((_, i) => (
            <Cell key={i} fill={COLORS[i % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip formatter={(value) => formatEuro(value)} />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
}
