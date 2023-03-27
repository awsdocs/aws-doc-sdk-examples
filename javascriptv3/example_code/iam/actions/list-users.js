/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.users.listUsersV3]
import { ListUsersCommand, IAMClient } from "@aws-sdk/client-iam";

const client = new IAMClient({});

export const main = async () => {
  const command = new ListUsersCommand({ MaxItems: 10 });

  try {
    const { Users } = await client.send(command);
    Users.forEach(({ UserName, CreateDate }) => {
      console.log(`${UserName} created on: ${CreateDate}`);
    });
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[iam.JavaScript.users.listUsersV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
