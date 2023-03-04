import { Button, SpaceBetween } from "@cloudscape-design/components";

import { useUiStore } from "./store-ui";
import { useAuthStore } from "./store-auth";

function WelcomeUser() {
  const { authStatus, authManager, currentUser, setAuthStatus } =
    useAuthStore();
  const {
    login: { setLoginModalVisible },
  } = useUiStore();

  return authStatus === "signed_in" ? (
    <SpaceBetween direction="horizontal" size="s">
      <div className="full-height-centered">
        Welcome, {currentUser?.username}.
      </div>
      <Button
        onClick={async () => {
          const authStatus = await authManager.signOut();
          setAuthStatus(authStatus.status);
        }}
      >
        Logout
      </Button>
    </SpaceBetween>
  ) : (
    <Button onClick={() => setLoginModalVisible(true)}>Login</Button>
  );
}

export default WelcomeUser;
