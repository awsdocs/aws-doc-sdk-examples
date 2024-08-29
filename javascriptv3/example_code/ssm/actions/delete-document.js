// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.deleteDocument]
import { DeleteDocumentCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to delete an SSM document.
 */
export const main = async ({ documentName }) => {
  const client = new SSMClient({});
  const command = new DeleteDocumentCommand({ DocumentName: documentName });
  try {
    await client.send(command);
    return { Deleted: true };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "MissingParameter") {
      console.warn(`${caught.message}. Did you provide this value?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.deleteDocument]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
