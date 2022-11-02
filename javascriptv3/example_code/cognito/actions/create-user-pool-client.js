/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CreateUserPoolClientCommand,
  CognitoIdentityProviderClient,
  ExplicitAuthFlowsType,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.CreateUserPoolClient] */
const createUserPoolClient = async (clientName, poolId) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new CreateUserPoolClientCommand({
    UserPoolId: poolId,
    ClientName: clientName,
    ExplicitAuthFlows: [
      ExplicitAuthFlowsType.ALLOW_ADMIN_USER_PASSWORD_AUTH,
      ExplicitAuthFlowsType.ALLOW_USER_PASSWORD_AUTH,
      ExplicitAuthFlowsType.ALLOW_REFRESH_TOKEN_AUTH
    ],
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.CreateUserPoolClient] */

export { createUserPoolClient };
