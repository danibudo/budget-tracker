import { SimpleGrid, Card, Text, Loader } from "@mantine/core";
import { useFetch } from "../../hooks/useFetch";
import { formatEuro } from "../../utils/format";

const CARDS = [
  { key: 'balance',       label: 'Balance',        dynamicColor: true  },
  { key: 'totalIncome',   label: 'Total Income',   color: 'green.7'    },
  { key: 'totalExpenses', label: 'Total Expenses', color: 'red.7'      },
];

export default function SummaryCards({ refreshKey }) {
  const { data, loading, error } = useFetch('/summary', [refreshKey]);

  if (loading) return <Loader />;
  if (error) return <Text c="red">{error}</Text>;
  if (!data) return null;

  return (
    <SimpleGrid cols={3}>
      {CARDS.map(({ key, label, color, dynamicColor }) => {
        const value = data[key];
        const resolvedColor = dynamicColor
          ? (parseFloat(value) >= 0 ? 'green.7' : 'red.7')
          : color;

        return (
          <Card key={key} withBorder radius="md" p="lg">
            <Text size="sm" c="dimmed" mb={4}>{label}</Text>
            <Text size="xl" fw={700} c={resolvedColor}>{formatEuro(value)}</Text>
          </Card>
        );
      })}
    </SimpleGrid>
  );
}
