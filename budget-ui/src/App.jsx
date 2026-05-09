import { useState, useCallback } from "react";
import { Tabs } from "@mantine/core";
import './App.css'

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

        <Tabs.Panel value="dashboard">
          <>Dashboard</>
        </Tabs.Panel>
        <Tabs.Panel value="transactions">
          <>Transactions</>
        </Tabs.Panel>
        <Tabs.Panel value="categories">
          <>Categories</>
        </Tabs.Panel>
      </Tabs>
    </>
  )
}

export default App
