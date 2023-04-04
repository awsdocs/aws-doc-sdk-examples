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

export interface Store {
  error: string;
  authStatus: AuthStatus;
  currentUser: User | null;
  token: string;
  autoLogout: <T>(fn: () => Promise<T>) => Promise<T>;
  checkAuth: () => void;
  signOut: () => void;
}

export const useStore = create<Store>((set, get) => ({
  error: "",
  authStatus: "signed_out",
  currentUser: null,
  token: "",
  labels: [],
  autoLogout: async (fn) => {
    try {
      return await fn();
    } catch (err) {
      console.error(err);

      if ((err as Error)?.message === "Unauthorized") {
        get().signOut();
      }
      throw err;
    }
  },
  checkAuth() {
    const params = new URLSearchParams(location.href.split("#")[1]);
    const idToken = params.get("id_token");
    const accessToken = params.get("access_token");
    if (idToken && accessToken) {
      const idTokenClaims = decodeJwt(idToken);
      set({
        currentUser: { username: idTokenClaims["cognito:username"] as string },
        token: idToken,
        authStatus: "signed_in",
      });
    } else {
      set({ token: "", currentUser: null, authStatus: "signed_out" });
    }
  },
  signOut() {
    location.assign(import.meta.env.VITE_COGNITO_SIGN_OUT_URL);
  },
}));
