/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  AssociateSoftwareTokenCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.AssociateSoftwareToken] */
const associateSoftwareToken = async (session) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);
  const command = new AssociateSoftwareTokenCommand({
    Session: session,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.AssociateSoftwareToken] */

export { associateSoftwareToken };
