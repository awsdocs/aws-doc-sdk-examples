export type AuthResult = AuthSuccess | AuthResetRequired | AuthSignOut;

export type AuthStatus = "signed_in" | "signed_out" | "reset_required";

export interface AuthSuccess {
  status: "signed_in";
  token: string;
}

export interface AuthResetRequired {
  status: "reset_required";
  userAttributes: unknown;
}

export interface AuthSignOut {
  status: "signed_out";
}

export interface User {
  username: string;
}

export interface AuthManager {
  getUser(): Promise<User | null>;
  resetPassword(username: string, password: string): Promise<AuthSuccess>;
  signIn(
    username: string,
    password: string
  ): Promise<AuthSuccess | AuthResetRequired>;
  signOut(): Promise<AuthSignOut>;
}
