/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.getRoleV3]
import { GetRoleCommand, IAMClient } from "@aws-sdk/client-iam";

const client = new IAMClient({});

export const main = async () => {
  const command = new GetRoleCommand({
    RoleName: "ROLE_NAME",
  });

  try {
    const { Role } = await client.send(command);
    console.log(Role);
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[iam.JavaScript.getRoleV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
