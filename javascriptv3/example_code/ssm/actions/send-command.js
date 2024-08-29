// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.sendCommand]
import { SendCommandCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates asynchronous requests to send an SSM command to a managed node.
 */
export const main = async ({ documentName }) => {
  const client = new SSMClient({});
  const command = new SendCommandCommand({
    DocumentName: documentName,
  });
  try {
    await client.send(command);
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
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
