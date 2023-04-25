/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_putItem.js demonstrates how to insert an item into an Amazon DynamoDB table using PartiQL.

Running the code:
node partiQL_putItem.js <tableName> <movieYear1> <movieTitle1>

For examples, node partiQL_putItem.js Movies 2006 'The Departed'
*/
// snippet-start:[dynamodb.JavaScript.partiQL.putItemV3]

// Import required AWS SDK clients and commands for Node.js.
import { ExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

const tableName = process.argv[2];
const movieYear1 = process.argv[3];
const movieTitle1 = process.argv[4];

export const run = async (tableName, movieTitle1, movieYear1) => {
  const params = {
    Statement: "INSERT INTO " + tableName + "  value  {'title':?, 'year':?}",
    Parameters: [{ S: movieTitle1 }, { N: movieYear1 }],
  };
  try {
    await ddbDocClient.send(new ExecuteStatementCommand(params));
    console.log("Success. Item added.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(tableName, movieTitle1, movieYear1);
// snippet-end:[dynamodb.JavaScript.partiQL.putItemV3]
