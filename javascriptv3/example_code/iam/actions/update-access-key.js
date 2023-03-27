/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[iam.JavaScript.keys.updateAccessKeyV3]
import { UpdateAccessKeyCommand, IAMClient, StatusType } from "@aws-sdk/client-iam";

const client = new IAMClient({});

export const main = async () => {
  const command = new UpdateAccessKeyCommand({
    AccessKeyId: "ACCESS_KEY_ID",
    Status: StatusType.Active,
    UserName: "USER_NAME",
  });

  try {
    const response = await client.send(command);
    console.log(response);
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[iam.JavaScript.keys.updateAccessKeyV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
