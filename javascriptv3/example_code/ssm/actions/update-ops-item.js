// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.updateOpsItem]
import { UpdateOpsItemCommand, SSMClient } from "@aws-sdk/client-ssm";
import { parseArgs } from "node:util";

/**
 * Update an SSM OpsItem.
 * @param {{ opsItemId: string, status?: OpsItemStatus }}
 */
export const main = async ({
  opsItemId,
  status = undefined, // The OpsItem status. Status can be Open, In Progress, or Resolved
}) => {
  const client = new SSMClient({});
  try {
    await client.send(
      new UpdateOpsItemCommand({
        OpsItemId: opsItemId,
        Status: status,
      }),
    );
    console.log("Ops item updated.");
    return { Success: true };
  } catch (caught) {
    if (
      caught instanceof Error &&
      caught.name === "OpsItemLimitExceededException"
    ) {
      console.warn(
        `Couldn't create ops item because you have exceeded your open OpsItem limit. ${caught.message}.`,
      );
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.updateOpsItem]
import { fileURLToPath } from "node:url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const options = {
    opsItemId: {
      type: "string",
    },
    status: {
      type: "OpsItemStatus",
    },
  };
  const { values } = parseArgs({ options });
  main(values);
}
