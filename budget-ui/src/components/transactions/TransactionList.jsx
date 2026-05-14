import { useState } from "react";
import { Table, Text, ActionIcon, Loader, ScrollArea } from "@mantine/core";
import { client } from "../../api/client";
import { useFetch } from "../../hooks/useFetch";
import { formatEuro, formatDate } from "../../utils/format";
import ConfirmModal from "../shared/ConfirmModal";
import TypeBadge from "../shared/TypeBadge";

const ROW_BG = {
  INCOME: "rgba(47, 158, 68, 0.08)",
  EXPENSE: "rgba(224, 49, 49, 0.08)",
};

export default function TransactionList({ refreshKey, triggerRefresh }) {
  const { data, loading, error } = useFetch("/transactions", [refreshKey]);
  const transactions = data ?? [];
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  async function handleDelete() {
    setDeleting(true);
    setDeleteError(null);
    try {
      await client.delete(`/transactions/${toDelete.id}`);
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
  if (transactions.length === 0) return <Text c="dimmed">No transactions yet.</Text>;

  return (
    <>
      <ScrollArea type="auto">
        <Table striped={false} highlightOnHover styles={{ th: { textAlign: 'center' } }}>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Date</Table.Th>
              <Table.Th>Description</Table.Th>
              <Table.Th>Category</Table.Th>
              <Table.Th>Type</Table.Th>
              <Table.Th>Amount</Table.Th>
              <Table.Th />
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {transactions.map(tx => (
              <Table.Tr key={tx.id} style={{ backgroundColor: ROW_BG[tx.type] }}>
                <Table.Td style={{ whiteSpace: 'nowrap' }}>{formatDate(tx.date)}</Table.Td>
                <Table.Td>{tx.description ?? "—"}</Table.Td>
                <Table.Td>{tx.categoryName}</Table.Td>
                <Table.Td style={{ whiteSpace: 'nowrap' }}><TypeBadge type={tx.type} /></Table.Td>
                <Table.Td style={{ whiteSpace: 'nowrap' }}>
                  <Text fw={500} c={tx.type === "INCOME" ? "green.7" : "red.7"}>
                    {tx.type === "INCOME" ? "+" : "-"}{formatEuro(tx.amount)}
                  </Text>
                </Table.Td>
                <Table.Td>
                  <ActionIcon variant="subtle" color="red" onClick={() => setToDelete(tx)} aria-label="Delete transaction">
                    ✕
                  </ActionIcon>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </ScrollArea>

      <ConfirmModal
        opened={toDelete !== null}
        onClose={closeModal}
        onConfirm={handleDelete}
        title="Delete transaction"
        loading={deleting}
        error={deleteError}
      >
        <Text mb="md">
          Are you sure you want to delete this transaction?
        </Text>
      </ConfirmModal>
    </>
  );
}
