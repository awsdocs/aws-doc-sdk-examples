import { Alert } from "@cloudscape-design/components";
import AppLayout from "@cloudscape-design/components/app-layout";
import { useEffect } from "react";

import "./App.css";
import LoginNavigation from "./LoginNavigation";
import { useAuthStore } from "./store-auth";
import TagsLayout from "./TagsLayout";

function App() {
  const { authStatus, checkAuth } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return (
    <>
      <LoginNavigation title="Photo Asset Management" />
      <AppLayout
        toolsHide={true}
        navigationHide={true}
        contentType="cards"
        content={<TagsLayout />}
        notifications={
          authStatus !== "signed_in" && (
            <Alert type="warning">
              Log in to view, upload, and download images.
            </Alert>
          )
        }
      />
    </>
  );
}

export default App;
