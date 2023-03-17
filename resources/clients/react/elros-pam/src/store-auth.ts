import { create } from "zustand";
import { decodeJwt } from "jose";

export type AuthResult = AuthSuccess | AuthSignOut;

export type AuthStatus = "signed_in" | "signed_out";

export interface AuthSuccess {
  status: "signed_in";
  token: string;
}

export interface AuthSignOut {
  status: "signed_out";
}

export interface User {
  username: string;
}

interface Tokens {
  accessToken: string;
  idToken: string;
}

export interface AuthStore {
  error: string;
  authStatus: AuthStatus;
  currentUser: User | null;
  token: string;
  checkAuth: () => void;
  signIn: (tokens: Tokens) => void;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  error: "",
  authStatus: "signed_out",
  currentUser: null,
  token: "",
  checkAuth() {
    const params = new URLSearchParams(location.href.split("#")[1]);
    const idToken = params.get("id_token");
    const accessToken = params.get("access_token");
    if (idToken && accessToken) {
      get().signIn({ idToken, accessToken });
    } else {
      set({ token: null, currentUser: null, authStatus: "signed_out" });
    }
  },
  signIn({ idToken, accessToken }: Tokens) {
    if (!(idToken && accessToken)) {
      throw new Error("Failed to sign in. Tokens are empty.");
    }

    const idTokenClaims = decodeJwt(idToken);
    set({
      currentUser: { username: idTokenClaims["cognito:username"] as string },
      token: idToken,
      authStatus: "signed_in",
    });
  },
  signOut() {
    set({ token: "", currentUser: null, authStatus: "signed_out" });
  },
}));
