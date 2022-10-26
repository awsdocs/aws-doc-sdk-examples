/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ListUsersCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.ListUsers] */
const listUsers = async ({ userPoolId }) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new ListUsersCommand({
    UserPoolId: userPoolId,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.ListUsers] */

export { listUsers };
