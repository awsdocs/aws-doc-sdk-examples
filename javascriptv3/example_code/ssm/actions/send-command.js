// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.sendCommand]
import { SendCommandCommand, SSMClient } from "@aws-sdk/client-ssm";
import { parseArgs } from "node:util";

/**
 * Send an SSM command to a managed node.
 * @param {{ documentName: string }}
 */
export const main = async ({ documentName }) => {
  const client = new SSMClient({});
  try {
    await client.send(
      new SendCommandCommand({
        DocumentName: documentName,
      }),
    );
    console.log("Command sent successfully.");
    return { Success: true };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "ValidationError") {
      console.warn(`${caught.message}. Did you provide a valid document name?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.sendCommand]
import { fileURLToPath } from "node:url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const options = {
    documentName: {
      type: "string",
    },
  };
  const { values } = parseArgs({ options });
  main(values);
}
