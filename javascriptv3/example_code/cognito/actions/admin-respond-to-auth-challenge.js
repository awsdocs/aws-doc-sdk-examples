import {
  CognitoIdentityProviderClient,
  AdminRespondToAuthChallengeCommand,
  ChallengeNameType,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.AdminRespondToAuthChallenge] */
const adminRespondToAuthChallenge = async ({
  userPoolId,
  clientId,
  username,
  totp,
  session,
}) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);
  const command = new AdminRespondToAuthChallengeCommand({
    ChallengeName: ChallengeNameType.SOFTWARE_TOKEN_MFA,
    ChallengeResponses: {
      SOFTWARE_TOKEN_MFA_CODE: totp,
      USERNAME: username,
    },
    ClientId: clientId,
    UserPoolId: userPoolId,
    Session: session,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.AdminRespondToAuthChallenge] */

export { adminRespondToAuthChallenge };
