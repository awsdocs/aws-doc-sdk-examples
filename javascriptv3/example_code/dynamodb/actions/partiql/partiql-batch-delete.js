/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[dynamodb.JavaScript.partiQL.deleteItemsV3]
import {
  BatchExecuteStatementCommand,
  DynamoDBClient,
} from "@aws-sdk/client-dynamodb";

const client = new DynamoDBClient({});

export const main = async () => {
  const command = new BatchExecuteStatementCommand({
    Statements: [
      {
        Statement: "DELETE FROM Flavors where Name=?",
        
        Parameters: [{ S: "Grape" }],
      },
      {
        Statement: "DELETE FROM Flavors where Name=?",
        Parameters: [{ S: "Strawberry" }],
      },
    ],
  });

  const response = await client.send(command);
  console.log(response);
  return response;
};
// snippet-end:[dynamodb.JavaScript.partiQL.deleteItemsV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
