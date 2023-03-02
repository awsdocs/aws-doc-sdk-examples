import { create } from "zustand";
import { AuthManager, AuthStatus, AuthResult, User } from "./auth";
import { CognitoAuthManager } from "./cognito-auth";

export interface AuthStore {
  error: string;
  authStatus: AuthStatus;
  authManager: AuthManager;
  currentUser: User | null;
  handleAuth: (authFn: () => Promise<AuthResult>) => void;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  error: "",
  authStatus: "signed_out",
  authManager: new CognitoAuthManager(),
  currentUser: null,
  handleAuth: async (authFn: () => Promise<AuthResult>) => {
    try {
      const { status } = await authFn();
      if (status === "signed_in") {
        set({ currentUser: await get().authManager.getUser() });
      }
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
