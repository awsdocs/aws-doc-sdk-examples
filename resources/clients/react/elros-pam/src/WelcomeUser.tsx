import { Button, SpaceBetween } from "@cloudscape-design/components";

import { useUiStore } from "./store-ui";
import { useAuthStore } from "./store-auth";

function WelcomeUser() {
  const { authStatus, authManager, currentUser, handleAuth } = useAuthStore();
  const {
    login: { setLoginModalVisible },
  } = useUiStore();

  return authStatus === "signed_in" ? (
    <SpaceBetween direction="horizontal" size="s">
      <div className="full-height-centered">
        Welcome, {currentUser?.username}.
      </div>
      <Button onClick={() => handleAuth(() => authManager.signOut())}>
        Logout
      </Button>
    </SpaceBetween>
  ) : (
    <Button onClick={() => setLoginModalVisible(true)}>Login</Button>
  );
}

export default WelcomeUser;
