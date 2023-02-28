import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button, { ButtonProps } from "@cloudscape-design/components/button";
import { Alert, FormField, Input, Modal } from "@cloudscape-design/components";
import { useState } from "react";
import { AuthStatus } from "./auth";
import { useAuthStore } from "./store-auth";
import { useUiStore } from "./store-ui";

function LoginModal() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const { authManager, authStatus, error, handleAuth } = useAuthStore();
  const {
    login: { loginModalVisible, setLoginModalVisible },
  } = useUiStore();

  const primaryButtonState: Record<AuthStatus, () => ButtonProps> = {
    reset_required: () => ({
      children: "Reset",
      disabled: newPassword !== confirmPassword,
      onClick: () =>
        handleAuth(() => authManager.resetPassword(username, newPassword)),
    }),
    signed_out: () => ({
      children: "Login",
      disabled: !(username && password),
      onClick: () =>
        handleAuth(async () => {
          const result = await authManager.signIn(username, password);
          setLoginModalVisible(false)
          return result;
        }),
    }),
    signed_in: () => ({
      children: "Logout",
      onClick: () => handleAuth(() => authManager.signOut()),
    }),
    failure: () => ({
      children: "Login",
      disabled: !(username && password),
      onClick: () => handleAuth(() => authManager.signIn(username, password)),
    }),
  };

  return (
    <Modal visible={loginModalVisible} header={"Login"} onDismiss={() => setLoginModalVisible(false)}>
      <form onSubmit={(e) => e.preventDefault()}>
        <Form
          actions={
            <SpaceBetween direction="horizontal" size="xs">
              <Button {...primaryButtonState[authStatus]()}></Button>
            </SpaceBetween>
          }
        >
          {["signed_out", "failure"].includes(authStatus) && (
            <>
              <FormField label="Username">
                <Input
                  onChange={({ detail }) => setUsername(detail.value)}
                  value={username}
                />
              </FormField>
              <FormField label={`Password`}>
                <Input
                  onChange={({ detail }) => setPassword(detail.value)}
                  value={password}
                  type="password"
                />
              </FormField>
              {authStatus === "failure" && (
                <Alert type="error" header="Error">
                  {error}
                </Alert>
              )}
            </>
          )}
          {authStatus === "reset_required" && (
            <>
              <FormField
                description="New password for Cognito login"
                label="New password"
              >
                <Input
                  onChange={({ detail }) => setNewPassword(detail.value)}
                  value={newPassword}
                  type="password"
                />
              </FormField>
              <FormField
                description="Confirm new password for Cognito login"
                label="Confirm New Password"
              >
                <Input
                  onChange={({ detail }) => {
                    setConfirmPassword(detail.value);
                  }}
                  value={confirmPassword}
                  invalid={
                    confirmPassword.length > 0 &&
                    confirmPassword !== newPassword
                  }
                  type="password"
                />
              </FormField>
            </>
          )}
          {authStatus === "signed_in" && (
            <>
              <p>Logged in!</p>
            </>
          )}
        </Form>
      </form>
    </Modal>
  );
}

export default LoginModal;
