/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.listattachedrolepoliciesV3]
import {
  ListAttachedRolePoliciesCommand,
  IAMClient,
} from "@aws-sdk/client-iam";

const client = new IAMClient({});

/**
 *
 * @param {string} roleName
 */
export const listAttachedRolePolicies = (roleName) => {
  const command = new ListAttachedRolePoliciesCommand({
    RoleName: roleName,
  });

  return client.send(command);
};
// snippet-end:[iam.JavaScript.listattachedrolepoliciesV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  listAttachedRolePolicies("ROLE_NAME");
}
