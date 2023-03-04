import { create } from "zustand";
import { AuthManager, AuthStatus, User } from "./auth";
import { CognitoAuthManager } from "./cognito-auth";

export interface AuthStore {
  error: string | null;
  authStatus: AuthStatus;
  authManager: AuthManager;
  currentUser: User | null;
  token: string | null;
  setAuthStatus: (authStatus: AuthStatus) => void;
  setError: (error: string | null) => void;
  setToken: (token: string | null) => void;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  error: "",
  authStatus: "signed_out",
  authManager: new CognitoAuthManager(),
  currentUser: null,
  token: null,
  setAuthStatus: (authStatus: AuthStatus) => {
    set({ authStatus });
  },
  setError(error: string | null) {
    set({ error });
  },
  setToken(token: string | null) {
    set({ token });
  },
}));
