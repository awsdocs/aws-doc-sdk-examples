/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  DeleteUserPoolCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";

/** snippet-start:[javascript.v3.cognito-idp.actions.DeleteUserPool] */
const deleteUserPool = (poolId) => {
  const client = new CognitoIdentityProviderClient({});

  const command = new DeleteUserPoolCommand({
    UserPoolId: poolId,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.DeleteUserPool] */

export { deleteUserPool };
