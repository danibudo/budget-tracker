import { useState } from "react";
import { Stack, TextInput, Select, NumberInput, Button, Text, Title, Box } from "@mantine/core";
import { DatePickerInput } from "@mantine/dates";
import { client } from "../../api/client";
import { useFetch } from "../../hooks/useFetch";
import { formatDate } from "../../utils/format";

const EMPTY = { amount: '', date: new Date(), description: '', categoryId: null };

export default function AddTransactionForm({ triggerRefresh }) {
  const [fields, setFields] = useState(EMPTY);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const { data: categories } = useFetch('/categories');

  const categoryOptions = (categories ?? []).map(cat => ({
    value: String(cat.id),
    label: `${cat.type === 'INCOME' ? '💰' : '💸'} ${cat.name}`,
  }));

  function set(key, value) {
    setFields(prev => ({ ...prev, [key]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!fields.amount || !fields.date || !fields.categoryId) return;

    setLoading(true);
    setError(null);
    try {
      await client.post('/transactions', {
        amount: String(fields.amount),
        date: formatDate(fields.date),
        description: fields.description.trim() || null,
        categoryId: Number(fields.categoryId),
      });
      setFields({ ...EMPTY, date: new Date() });
      triggerRefresh();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  const canSubmit = fields.amount > 0 && fields.date && fields.categoryId;

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Title order={4} mb="sm">Add Transaction</Title>
      <Stack gap="sm">
        <NumberInput
          label="Amount"
          placeholder="0.00"
          min={0.01}
          decimalScale={2}
          value={fields.amount}
          onChange={val => set('amount', val)}
          required
        />
        <DatePickerInput
          label="Date"
          value={fields.date}
          onChange={val => set('date', val)}
          required
        />
        <TextInput
          label="Description"
          placeholder="Optional"
          value={fields.description}
          onChange={e => set('description', e.currentTarget.value)}
        />
        <Select
          label="Category"
          placeholder="Select category"
          data={categoryOptions}
          value={fields.categoryId}
          onChange={val => set('categoryId', val)}
          allowDeselect={false}
          required
        />
        {error && <Text c="red" size="sm">{error}</Text>}
        <Button type="submit" loading={loading} disabled={!canSubmit}>
          Add Transaction
        </Button>
      </Stack>
    </Box>
  );
}
