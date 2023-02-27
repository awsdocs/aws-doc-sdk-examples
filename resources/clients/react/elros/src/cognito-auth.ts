import {
  AuthenticationDetails,
  CognitoUser,
  CognitoUserPool,
} from "amazon-cognito-identity-js";
import { AuthManager, AuthResult } from "./auth";

export class CognitoAuthManager implements AuthManager {
  private _cognitoUser: CognitoUser | null = null;
  private _userPool = new CognitoUserPool({
    UserPoolId: import.meta.env.VITE_COGNITO_USER_POOL_ID,
    ClientId: import.meta.env.VITE_COGNITO_USER_POOL_CLIENT_ID,
  });

  private initCognitoUser(username: string) {
    this._cognitoUser = new CognitoUser({
      Username: username,
      Pool: this._userPool,
    });

    return this._cognitoUser;
  }

  private getCognitoUser() {
    return this._cognitoUser;
  }

  private getAuthDetails(u: string, p: string) {
    return new AuthenticationDetails({
      Username: u,
      Password: p,
    });
  }

  getUser() {
    const username = this._cognitoUser?.getUsername();

    return username ? { username } : null;
  }

  resetPassword(username: string, newPassword: string): Promise<AuthResult> {
    const cognitoUser = this.getCognitoUser();

    if (!cognitoUser) {
      throw new Error("Failed to reset password. Cognito user is missing.");
    }

    return new Promise<AuthResult>((resolve, reject) => {
      cognitoUser.completeNewPasswordChallenge(
        newPassword,
        {},
        {
          onSuccess: async () => {
            try {
              const authResult = await this.signIn(username, newPassword);
              if (authResult.status === "signed_in") {
                resolve(authResult);
              } else {
                reject(authResult);
              }
            } catch (err) {
              reject(new Error("Failed to reset password."));
            }
          },
          onFailure: reject,
        }
      );
    });
  }

  signIn(username: string, password: string): Promise<AuthResult> {
    const authDetails = this.getAuthDetails(username, password);
    const cognitoUser = this.initCognitoUser(username);

    return new Promise((resolve, reject) => {
      cognitoUser.authenticateUser(authDetails, {
        onSuccess: (session) => {
          resolve({
            status: "signed_in",
            token: session.getIdToken().getJwtToken(),
          });
        },
        onFailure: (err) => {
          reject(new Error("Incorrect username or password."));
        },
        newPasswordRequired: (userAttributes) => {
          resolve({
            status: "reset_required",
            userAttributes,
          });
        },
      });
    });
  }

  signOut(): Promise<AuthResult> {
    return new Promise((resolve) => {
      const currentUser = this.getCognitoUser();
      currentUser
        ? currentUser.signOut(() => resolve({ status: "signed_out" }))
        : resolve({ status: "signed_out" });
    });
  }
}
