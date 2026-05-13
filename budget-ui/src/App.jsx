import { useState, useCallback } from "react";
import { Tabs, Stack } from "@mantine/core";
import './App.css'
import AddCategoryForm from "./components/categories/AddCategoryForm";
import CategoryList from "./components/categories/CategoryList";
import AddTransactionForm from "./components/transactions/AddTransactionForm";
import TransactionList from "./components/transactions/TransactionList";
import SummaryCards from "./components/dashboard/SummaryCards";

function App() {
  const [refreshKey, setRefreshKey] = useState(0);
  const triggerRefresh = useCallback(() => setRefreshKey(k => k + 1), []);

  return (
    <>
      <Tabs defaultValue="dashboard">
        <Tabs.List>
          <Tabs.Tab value="dashboard">Dashboard</Tabs.Tab>
          <Tabs.Tab value="transactions">Transactions</Tabs.Tab>
          <Tabs.Tab value="categories">Categories</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="dashboard" p="md">
          <Stack gap="xl">
            <SummaryCards refreshKey={refreshKey} />
          </Stack>
        </Tabs.Panel>
        <Tabs.Panel value="transactions" p="md">
          <Stack gap="xl">
            <AddTransactionForm triggerRefresh={triggerRefresh} />
            <TransactionList refreshKey={refreshKey} triggerRefresh={triggerRefresh} />
          </Stack>
        </Tabs.Panel>
        <Tabs.Panel value="categories" p="md">
          <Stack gap="xl">
            <AddCategoryForm triggerRefresh={triggerRefresh} />
            <CategoryList refreshKey={refreshKey} triggerRefresh={triggerRefresh} />
          </Stack>
        </Tabs.Panel>
      </Tabs>
    </>
  )
}

export default App
