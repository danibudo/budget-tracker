import { useState } from "react";
import { Stack, TextInput, Select, Group, Box, Button, Text, Title } from "@mantine/core";
import { client } from "../../api/client";

const SWATCHES = ['#e03131', '#2f9e44', '#1971c2', '#f08c00', '#7048e8', '#099268', '#e64980', '#1098ad'];

const TYPE_OPTIONS = [
  { value: 'EXPENSE', label: 'Expense' },
  { value: 'INCOME', label: 'Income' },
];

const EMPTY = { name: '', type: null, color: null };

export default function AddCategoryForm({ triggerRefresh }) {
  const [fields, setFields] = useState(EMPTY);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  function set(key, value) {
    setFields(prev => ({ ...prev, [key]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!fields.name.trim() || !fields.type || !fields.color) return;

    setLoading(true);
    setError(null);
    try {
      await client.post('/categories', { name: fields.name.trim(), type: fields.type, color: fields.color });
      setFields(EMPTY);
      triggerRefresh();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Title order={4} mb="sm">Add Category</Title>
      <Stack gap="sm">
        <TextInput
          label="Name"
          placeholder="e.g. Groceries"
          value={fields.name}
          onChange={e => set('name', e.currentTarget.value)}
          required
        />
        <Select
          label="Type"
          placeholder="Select type"
          data={TYPE_OPTIONS}
          value={fields.type}
          onChange={val => set('type', val)}
          required
        />
        <Box>
          <Text size="sm" fw={500} mb={6}>Color</Text>
          <Group gap={8}>
            {SWATCHES.map(hex => (
              <Box
                key={hex}
                onClick={() => set('color', hex)}
                style={{
                  width: 28,
                  height: 28,
                  borderRadius: '50%',
                  backgroundColor: hex,
                  cursor: 'pointer',
                  outline: fields.color === hex ? `3px solid ${hex}` : '3px solid transparent',
                  outlineOffset: 2,
                }}
              />
            ))}
          </Group>
        </Box>
        {error && <Text c="red" size="sm">{error}</Text>}
        <Button type="submit" loading={loading} disabled={!fields.name.trim() || !fields.type || !fields.color}>
          Add Category
        </Button>
      </Stack>
    </Box>
  );
}
