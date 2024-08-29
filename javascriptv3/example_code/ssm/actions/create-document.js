// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.createDocument]
import { CreateDocumentCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to create an SSM document.
 */
export const main = async ({ content, name, documentType }) => {
  const client = new SSMClient({});
  const command = new CreateDocumentCommand({
    Content: content,
    Name: name,
    DocumentType: documentType,
  });
  try {
    const { documentDescription } = await client.send(command);
    return { DocumentDescription: documentDescription };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "DocumentAlreadyExists") {
      console.warn(`${caught.message}. Did you provide a new document name?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.createDocument]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
