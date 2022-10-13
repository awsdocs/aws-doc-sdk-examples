import {
  AdminGetUserCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.AdminGetUser] */
const adminGetUser = async ({ userPoolId, username }) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new AdminGetUserCommand({
    UserPoolId: userPoolId,
    Username: username,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.AdminGetUser] */

export { adminGetUser };
