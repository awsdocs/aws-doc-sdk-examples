import ContentLayout from "@cloudscape-design/components/content-layout";
import AppLayout from "@cloudscape-design/components/app-layout";
import Header from "@cloudscape-design/components/header";

import LoginModal from "./LoginModal";
import { useAuthStore } from "./store";
import { useEffect, useState } from "react";
import { Button, Grid, SpaceBetween } from "@cloudscape-design/components";

function App() {
  const { authStatus, authManager, handleAuth } = useAuthStore();
  const [loginVisible, setLoginVisible] = useState(false);

  useEffect(() => {
    setLoginVisible(authStatus !== "signed_in");
  }, [authStatus]);

  return (
    <AppLayout
      toolsHide={true}
      navigationHide={true}
      content={
        <ContentLayout
          header={
            <Header
              variant="h1"
              description="A descendant of Elwing"
              actions={
                authStatus === "signed_in" ? (
                  <div>
                    Welcome {authManager.getUser()?.username}
                    <Button
                      onClick={() => handleAuth(() => authManager.signOut())}
                    >
                      Logout
                    </Button>
                  </div>
                ) : (
                  <Button onClick={() => setLoginVisible(true)}>Login</Button>
                )
              }
            >
              Elros
            </Header>
          }
        >
          <LoginModal show={loginVisible} />
        </ContentLayout>
      }
    ></AppLayout>
  );
}

export default App;
