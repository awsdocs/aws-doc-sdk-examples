/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CognitoIdentityProviderClient,
  ConfirmDeviceCommand,
} from "@aws-sdk/client-cognito-identity-provider";
import { createClientForDefaultRegion } from "../../libs/utils/util-aws-sdk.js";

/** snippet-start:[javascript.v3.cognito-idp.actions.ConfirmDevice] */
const confirmDevice = async ({ deviceKey, accessToken, passwordVerifier, salt }) => {
  const client = createClientForDefaultRegion(CognitoIdentityProviderClient);

  const command = new ConfirmDeviceCommand({
    DeviceKey: deviceKey,
    AccessToken: accessToken,
    DeviceSecretVerifierConfig: {
      PasswordVerifier: passwordVerifier,
      Salt: salt
    },
  });

  return client.send(command);
};
/** snippet-end:[javascript.v3.cognito-idp.actions.ConfirmDevice] */

export { confirmDevice };
