import { Alert } from "@cloudscape-design/components";
import AppLayout from "@cloudscape-design/components/app-layout";
import { useEffect } from "react";

import "./App.css";
import HelpPanel from "./HelpPanel";
import LoginNavigation from "./LoginNavigation";
import { useStore } from "./store";

function App() {
  const { authStatus, checkAuth } = useStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return (
    <>
      <LoginNavigation title="Elros" />
      <AppLayout
        toolsHide={true}
        navigationHide={true}
        contentType="cards"
        content={<HelpPanel />}
        notifications={
          authStatus !== "signed_in" && (
            <Alert type="warning">
              You are not logged in.
            </Alert>
          )
        }
      />
    </>
  );
}

export default App;
