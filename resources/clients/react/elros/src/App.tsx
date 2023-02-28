import { lazy, Suspense } from "react";
import ContentLayout from "@cloudscape-design/components/content-layout";
import AppLayout from "@cloudscape-design/components/app-layout";
import Header from "@cloudscape-design/components/header";
import { Box } from "@cloudscape-design/components";

import "./App.css";
import { useUiStore } from "./store-ui";

const LazyLoginModal = lazy(() => import("./LoginModal"));
const LazyWelcomeUser = lazy(() => import("./WelcomeUser"));

function App() {
  const {
    login: { enabled: loginEnabled },
  } = useUiStore();

  return (
    <AppLayout
      toolsHide={true}
      navigationHide={true}
      content={
        <ContentLayout
          header={
            <Box padding={{ top: "s" }}>
              <Header
                variant="h1"
                description="A descendant of Elwing"
                actions={
                  // This is potentially a very common pattern that we could abstract into an HOC.
                  // <Feature enabled={loginEnabled}><LazyComponent /></Feature> maybe?
                  loginEnabled && (
                    <Suspense>
                      <LazyWelcomeUser />
                    </Suspense>
                  )
                }
              >
                Elros
              </Header>
            </Box>
          }
        >
          {loginEnabled && (
            <Suspense>
              <LazyLoginModal />
            </Suspense>
          )}
        </ContentLayout>
      }
    ></AppLayout>
  );
}

export default App;
