/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CognitoIdentityProviderClient,
  AdminRespondToAuthChallengeCommand,
  ChallengeNameType,
} from "@aws-sdk/client-cognito-identity-provider";

/** snippet-start:[javascript.v3.cognito-idp.actions.AdminRespondToAuthChallenge] */
const adminRespondToAuthChallenge = ({
  userPoolId,
  clientId,
  username,
  totp,
  session,
}) => {
  const client = new CognitoIdentityProviderClient({});
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
