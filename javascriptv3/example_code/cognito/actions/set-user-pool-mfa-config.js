/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  SetUserPoolMfaConfigCommand,
  CognitoIdentityProviderClient,
  UserPoolMfaType,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.SetUserPoolMfaConfig] */
const setUserPoolMfaConfig = async (poolId) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new SetUserPoolMfaConfigCommand({
    UserPoolId: poolId,
    MfaConfiguration: UserPoolMfaType.ON,
    SoftwareTokenMfaConfiguration: { Enabled: true },
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.SetUserPoolMfaConfig] */

export { setUserPoolMfaConfig };
