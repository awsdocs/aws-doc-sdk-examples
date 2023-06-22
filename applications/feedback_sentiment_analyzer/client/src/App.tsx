import { Alert } from "@cloudscape-design/components";
import AppLayout from "@cloudscape-design/components/app-layout";
import { useEffect } from "react";

import "./App.css";
import LoginNavigation from "./LoginNavigation";
import { useStore } from "./store";
import FileUpload from "./FileUpload";

function App() {
  const { authStatus, checkAuth, uploadFile } = useStore();

  const handleUpload = async (file: File) => {
    await uploadFile(file);
  };

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
        content={
          <FileUpload disabled={false} onSubmit={handleUpload}></FileUpload>
        }
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

export { App };
