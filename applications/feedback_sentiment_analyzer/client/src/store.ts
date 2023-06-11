import { create } from "zustand";
import { decodeJwt } from "jose";

import { COGNITO_SIGN_OUT_URL } from "./env";
import * as API from "./api";

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

export interface Store {
  error: string;
  authStatus: AuthStatus;
  currentUser: User | null;
  token: string;
  autoLogout: <T>(fn: () => Promise<T>) => Promise<T>;
  checkAuth: () => void;
  signOut: () => void;
  uploadFile: (file: File) => Promise<Response>;
}

export const useStore = create<Store>((set, get) => ({
  error: "",
  authStatus: "signed_out",
  currentUser: null,
  token: "",
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
    location.assign(`${COGNITO_SIGN_OUT_URL}&logout_uri=${location}`); // append ?redirect_uri=...
  },
  async uploadFile(file: File) {
    return get().autoLogout(() => API.uploadFile(file, { token: get().token }));
  },
}));
