import { create } from "zustand";
import { AuthManager, AuthStatus, AuthResult } from "./auth";
import { CognitoAuthManager } from "./cognito-auth";

export interface AuthStore {
  error: string;
  authStatus: AuthStatus;
  authManager: AuthManager;
  handleAuth: (authFn: () => Promise<AuthResult>) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
  error: "",
  authStatus: "signed_out",
  authManager: new CognitoAuthManager(),
  handleAuth: async (authFn: () => Promise<AuthResult>) => {
    try {
      const { status } = await authFn();
      set({ authStatus: status });
    } catch (err) {
      console.log(err);
      useAuthStore.setState({ authStatus: "failure" });
      set({
        error:
          err instanceof Error
            ? err.message
            : typeof err === "string"
            ? err
            : "Unknown error",
      });
    }
  },
}));
