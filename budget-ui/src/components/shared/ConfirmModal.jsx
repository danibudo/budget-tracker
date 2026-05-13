import { Modal, Text, Group, Button } from "@mantine/core";

export default function ConfirmModal({
  opened,
  onClose,
  onConfirm,
  title,
  children,
  loading = false,
  error = null,
  confirmLabel = "Delete",
  confirmColor = "red",
}) {
  return (
    <Modal opened={opened} onClose={onClose} title={title} centered>
      {children}
      {error && <Text c="red" size="sm" mb="sm">{error}</Text>}
      <Group justify="flex-end" mt="md">
        <Button variant="default" onClick={onClose} disabled={loading}>Cancel</Button>
        <Button color={confirmColor} onClick={onConfirm} loading={loading}>{confirmLabel}</Button>
      </Group>
    </Modal>
  );
}
