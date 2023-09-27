/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  AssociateSoftwareTokenCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";

/** snippet-start:[javascript.v3.cognito-idp.actions.AssociateSoftwareToken] */
const associateSoftwareToken = (session) => {
  const client = new CognitoIdentityProviderClient({});
  const command = new AssociateSoftwareTokenCommand({
    Session: session,
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.AssociateSoftwareToken] */

export { associateSoftwareToken };
