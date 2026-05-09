import { useState } from "react";
import { Stack, Group, Text, Badge, ActionIcon, Loader, Modal, Button, Box } from "@mantine/core";
import { client } from "../../api/client";
import { useFetch } from "../../hooks/useFetch";

export default function CategoryList({ refreshKey, triggerRefresh }) {
  const { data, loading, error } = useFetch('/categories', [refreshKey]);
  const categories = data ?? [];
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  async function handleDelete() {
    setDeleting(true);
    setDeleteError(null);
    try {
      await client.delete(`/categories/${toDelete.id}`);
      setToDelete(null);
      triggerRefresh();
    } catch (err) {
      setDeleteError(err.message);
    } finally {
      setDeleting(false);
    }
  }

  function closeModal() {
    setToDelete(null);
    setDeleteError(null);
  }

  if (loading) return <Loader />;
  if (error) return <Text c="red">{error}</Text>;
  if (categories.length === 0) return <Text c="dimmed">No categories yet.</Text>;

  return (
    <>
      <Stack gap="xs">
        {categories.map(cat => (
          <Group key={cat.id} justify="space-between" p="sm" style={{ border: '1px solid var(--mantine-color-default-border)', borderRadius: 8 }}>
            <Group gap="sm">
              <Box style={{ width: 14, height: 14, borderRadius: '50%', backgroundColor: cat.color, flexShrink: 0 }} />
              <Text fw={500}>{cat.name}</Text>
              <Badge color={cat.type === 'INCOME' ? 'green' : 'red'} variant="light">
                {cat.type}
              </Badge>
            </Group>
            <ActionIcon variant="subtle" color="red" onClick={() => setToDelete(cat)} aria-label="Delete category">
              ✕
            </ActionIcon>
          </Group>
        ))}
      </Stack>

      <Modal
        opened={toDelete !== null}
        onClose={closeModal}
        title="Delete category"
        centered
      >
        <Text mb="md">
          Are you sure you want to delete <strong>{toDelete?.name}</strong>?{' '}
          All transactions in this category will also be deleted.
        </Text>
        {deleteError && <Text c="red" size="sm" mb="sm">{deleteError}</Text>}
        <Group justify="flex-end">
          <Button variant="default" onClick={closeModal} disabled={deleting}>Cancel</Button>
          <Button color="red" onClick={handleDelete} loading={deleting}>Delete</Button>
        </Group>
      </Modal>
    </>
  );
}
