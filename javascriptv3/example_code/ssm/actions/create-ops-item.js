// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.Basics.createOpsItem]
import { CreateOpsItemCommand, SSMClient } from "@aws-sdk/client-ssm";

/**
 * This method initiates an asynchronous request to create an SSM OpsItem.
 */
export const main = async ({
  title,
  source,
  category = undefined,
  severity = undefined,
}) => {
  const client = new SSMClient({});
  const command = new CreateOpsItemCommand({
    Title: title,
    Source: source,
    Category: category,
    Severity: severity,
  });
  try {
    const { opsItemArn, opsItemId } = await client.send(command);
    return { OpsItemArn: opsItemArn, OpsItemId: opsItemId };
  } catch (caught) {
    if (caught instanceof Error && caught.name === "MissingParameter") {
      console.warn(`${caught.message}. Did you provide these values?`);
    } else {
      throw caught;
    }
  }
};
// snippet-end:[ssm.JavaScript.Basics.createOpsItem]
import { fileURLToPath } from "url";
// Call function if run directly
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
