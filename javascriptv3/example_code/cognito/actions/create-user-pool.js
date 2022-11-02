/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CreateUserPoolCommand,
  CognitoIdentityProviderClient,
  VerifiedAttributeType,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.CreateUserPool] */
const createUserPool = async (poolName, configOverrides = {}) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new CreateUserPoolCommand({
    PoolName: poolName,
    AutoVerifiedAttributes: [VerifiedAttributeType.EMAIL],
    Schema: [{ Name: "email", Required: true }],
    UsernameConfiguration: { CaseSensitive: false },
    ...configOverrides,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.CreateUserPool] */

export { createUserPool };
