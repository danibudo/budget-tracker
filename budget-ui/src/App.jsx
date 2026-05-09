import { Tabs } from "@mantine/core";
import './App.css'

function App() {

  return (
    <>
      <Tabs>
        <Tabs.List default="dashboard">
          <Tabs.Tab value="dashboard">
            Dashboard
          </Tabs.Tab>
          <Tabs.Tab value="transactions">
            Transactions
          </Tabs.Tab>
          <Tabs.Tab value="categories">
            Categories
          </Tabs.Tab>
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
