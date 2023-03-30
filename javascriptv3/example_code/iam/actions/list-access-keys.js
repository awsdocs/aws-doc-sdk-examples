/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.keys.listAccessKeysV3]
import { ListAccessKeysCommand, IAMClient } from "@aws-sdk/client-iam";

const client = new IAMClient({});

/**
 *
 * @param {string} userName
 */
export const listAccessKeys = async (userName) => {
  const command = new ListAccessKeysCommand({
    MaxItems: 5,
    UserName: userName,
  });

  const response = await client.send(command);
  console.log(response.AccessKeyMetadata.map((x) => x.AccessKeyId).join("\n"));
  return response;
};
// snippet-end:[iam.JavaScript.keys.listAccessKeysV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  listAccessKeys("USER_NAME");
}
