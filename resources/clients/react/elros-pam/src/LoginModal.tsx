import Form from "@cloudscape-design/components/form";
import SpaceBetween from "@cloudscape-design/components/space-between";
import Button, { ButtonProps } from "@cloudscape-design/components/button";
import { Alert, FormField, Input, Modal } from "@cloudscape-design/components";
import { useState } from "react";
import { AuthStatus, AuthSuccess } from "./auth";
import { useAuthStore } from "./store-auth";
import { useUiStore } from "./store-ui";

function LoginModal() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const {
    authManager,
    authStatus,
    error,
    setAuthStatus,
    setError,
    setToken,
    setCurrentUser,
  } = useAuthStore();
  const {
    login: { loginModalVisible, setLoginModalVisible },
  } = useUiStore();

  const handleSignIn = async (result: AuthSuccess) => {
    const user = await authManager.getUser();
    setLoginModalVisible(false);
    setError(null);
    setCurrentUser(user);
    setAuthStatus(result.status);
    setToken(result.token);
  };

  const primaryButtonState: Record<AuthStatus, () => ButtonProps> = {
    reset_required: () => ({
      children: "Reset",
      disabled: newPassword !== confirmPassword,
      onClick: async () => {
        try {
          const authResult: AuthSuccess = await authManager.resetPassword(
            username,
            newPassword
          );
          await handleSignIn(authResult);
        } catch (err) {
          console.log(err);
          setError("Failed to reset password.");
        }
      },
    }),
    signed_out: () => ({
      children: "Login",
      disabled: !(username && password),
      onClick: async () => {
        try {
          const result = await authManager.signIn(username, password);
          setError(null);
          if (result.status !== "reset_required") {
            await handleSignIn(result);
          }
        } catch (err) {
          console.log(err);
          setError("Login attempt failed.");
        }
      },
    }),
    signed_in: () => ({
      children: "Logout",
      onClick: async () => {
        try {
          const result = await authManager.signOut();
          setAuthStatus(result.status);
          setToken(null);
        } catch (err) {
          console.log(err);
        }
      },
    }),
  };

  return (
    <Modal
      visible={loginModalVisible}
      header={"Login"}
      onDismiss={() => setLoginModalVisible(false)}
    >
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
              {error && (
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
        </Form>
      </form>
    </Modal>
  );
}

export default LoginModal;
