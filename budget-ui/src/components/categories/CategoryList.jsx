import { useState } from "react";
import { Stack, Group, Text, ActionIcon, Loader, Box } from "@mantine/core";
import { client } from "../../api/client";
import { useFetch } from "../../hooks/useFetch";
import ConfirmModal from "../shared/ConfirmModal";
import TypeBadge from "../shared/TypeBadge";

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
              <TypeBadge type={cat.type} />
            </Group>
            <ActionIcon variant="subtle" color="red" onClick={() => setToDelete(cat)} aria-label="Delete category">
              ✕
            </ActionIcon>
          </Group>
        ))}
      </Stack>

      <ConfirmModal
        opened={toDelete !== null}
        onClose={closeModal}
        onConfirm={handleDelete}
        title="Delete category"
        loading={deleting}
        error={deleteError}
      >
        <Text mb="md">
          Are you sure you want to delete <strong>{toDelete?.name}</strong>?{' '}
          All transactions in this category will also be deleted.
        </Text>
      </ConfirmModal>
    </>
  );
}
