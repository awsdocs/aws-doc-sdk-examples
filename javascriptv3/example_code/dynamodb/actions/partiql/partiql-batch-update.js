/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[dynamodb.JavaScript.partiQL.updateItemsV3]
import {
  BatchExecuteStatementCommand,
  DynamoDBClient,
} from "@aws-sdk/client-dynamodb";

const client = new DynamoDBClient({});

export const main = async () => {
  const eggUpdates = [
    ["duck", "fried"],
    ["chicken", "omelette"],
  ];
  const command = new BatchExecuteStatementCommand({
    Statements: eggUpdates.map((change) => ({
      Statement: "UPDATE Eggs SET Style=? where Variety=?",
      Parameters: [{ S: change[1] }, { S: change[0] }],
    })),
  });

  const response = await client.send(command);
  const errors = response.Responses.map((r) => r.Error).filter((e) => !!e);
  if (errors.length) {
    errors.forEach((e) => console.log(`${e.Code} - ${e.Message}`));
  }
  console.log(response);
  return response;
};
// snippet-end:[dynamodb.JavaScript.partiQL.updateItemsV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
