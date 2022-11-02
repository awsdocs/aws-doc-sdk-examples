/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  AuthFlowType,
  CognitoIdentityProviderClient,
  InitiateAuthCommand,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.InitiateAuth] */
const initiateAuth = async ({ username, password, clientId }) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new InitiateAuthCommand({
    AuthFlow: AuthFlowType.USER_PASSWORD_AUTH,
    AuthParameters: {
      USERNAME: username,
      PASSWORD: password,
    },
    ClientId: clientId,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.InitiateAuth] */

export { initiateAuth };
