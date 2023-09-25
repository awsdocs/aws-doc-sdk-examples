/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CreateUserPoolClientCommand,
  CognitoIdentityProviderClient,
  ExplicitAuthFlowsType,
} from "@aws-sdk/client-cognito-identity-provider";

/** snippet-start:[javascript.v3.cognito-idp.actions.CreateUserPoolClient] */
const createUserPoolClient = (clientName, poolId) => {
  const client = new CognitoIdentityProviderClient({});

  const command = new CreateUserPoolClientCommand({
    UserPoolId: poolId,
    ClientName: clientName,
    ExplicitAuthFlows: [
      ExplicitAuthFlowsType.ALLOW_ADMIN_USER_PASSWORD_AUTH,
      ExplicitAuthFlowsType.ALLOW_USER_PASSWORD_AUTH,
      ExplicitAuthFlowsType.ALLOW_REFRESH_TOKEN_AUTH,
    ],
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.CreateUserPoolClient] */

export { createUserPoolClient };
