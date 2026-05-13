import { Text, Loader, Stack, Title } from "@mantine/core";
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { useFetch } from "../../hooks/useFetch";
import { formatEuro } from "../../utils/format";

export default function MonthlyBarChart({ refreshKey }) {
  const { data, loading, error } = useFetch('/summary/monthly', [refreshKey]);

  if (loading) return <Loader />;
  if (error) return <Text c="red">{error}</Text>;

  const items = (data ?? []).map(item => ({
    ...item,
    income: parseFloat(item.income),
    expenses: parseFloat(item.expenses),
  }));

  if (items.length === 0) return <Text c="dimmed">No monthly data to display.</Text>;

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Stack gap={4} mb="sm">
        <Title order={5}>Monthly income vs. expenses</Title>
        <Text size="xs" c="dimmed">Income and expenses totals grouped by month</Text>
      </Stack>
      <div style={{ flex: 1, display: 'flex', alignItems: 'center' }}>
        <ResponsiveContainer width="100%" aspect={2.5}>
          <BarChart data={items}>
            <XAxis dataKey="month" />
            <YAxis tickFormatter={(v) => formatEuro(v)} width={100} />
            <Tooltip formatter={(value) => formatEuro(value)} />
            <Legend />
            <Bar dataKey="income" fill="#1971c2" />
            <Bar dataKey="expenses" fill="#e03131" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
