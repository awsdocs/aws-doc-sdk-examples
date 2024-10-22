// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.deleteMaintenanceWindow]
import { DeleteMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";
import { parseArgs } from "node:util";

/**
 * Delete an SSM maintenance window.
 * @param {{ windowId: string }}
 */
export const main = async ({ windowId }) => {
  const client = new SSMClient({});
  try {
    await client.send(
      new DeleteMaintenanceWindowCommand({ WindowId: windowId }),
    );
    console.log(`Maintenance window '${windowId}' deleted.`);
    return { Deleted: true };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "MissingParameter") {
      console.warn(`${caught.message}. Did you provide this value?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.deleteMaintenanceWindow]
import { fileURLToPath } from "node:url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const options = {
    windowId: {
      type: "string",
    },
  };
  const { values } = parseArgs({ options });
  main(values);
}
