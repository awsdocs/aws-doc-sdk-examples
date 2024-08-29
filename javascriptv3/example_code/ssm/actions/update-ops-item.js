// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.updateOpsItem]
import { UpdateOpsItemCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to update an SSM OpsItem.
 */
export const main = async ({ opsItemId, status }) => {
  const client = new SSMClient({});
  const command = new UpdateOpsItemCommand({
    OpsItemId: opsItemId,
    Status: status,
  });
  try {
    await client.send(command);
    return { Success: true };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "MissingParameter") {
      console.warn(`${caught.message}. Did you provide these values?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.updateOpsItem]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
