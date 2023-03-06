import { Alert } from "@cloudscape-design/components";
import AppLayout from "@cloudscape-design/components/app-layout";

import "./App.css";
import LoginModal from "./LoginModal";
import LoginNavigation from "./LoginNavigation";
import { useAuthStore } from "./store-auth";
import TagsLayout from "./TagsLayout";

function App() {
  const { authStatus } = useAuthStore();

  return (
    <>
      <LoginNavigation title="Photo Archive" />
      <LoginModal />
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
      ></AppLayout>
    </>
  );
}

export default App;
