/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ResendConfirmationCodeCommand,
  CognitoIdentityProviderClient,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.ResendConfirmationCode] */
const resendConfirmationCode = async ({ clientId, username }) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new ResendConfirmationCodeCommand({
    ClientId: clientId,
    Username: username
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.ResendConfirmationCode] */

export { resendConfirmationCode };
