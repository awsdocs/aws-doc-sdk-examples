export type AuthResult = AuthSuccess | AuthFailure | AuthResetRequired | AuthSignOut;

export type AuthStatus =
  | "signed_in"
  | "signed_out"
  | "reset_required"
  | "failure";

export interface AuthSuccess {
  status: "signed_in";
  token: string;
}

export interface AuthFailure {
  status: "failure";
  error: Error;
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
  resetPassword(username: string, password: string): Promise<AuthResult>;
  signIn(username: string, password: string): Promise<AuthResult>;
  signOut(): Promise<AuthResult>;
}
