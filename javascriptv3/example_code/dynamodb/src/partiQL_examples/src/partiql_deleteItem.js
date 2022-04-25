/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_deleteItem.js demonstrates how to delete an item from an Amazon DynamoDB table using PartiQL.

Running the code:
node partiql_deleteItem.js <tableName> <movieYear1> <movieTitle1>

For example, node Movies 2006 'The Departed'
*/
// snippet-start:[dynamodb.JavaScript.partiQL.deleteItemV3]

// Import required AWS SDK clients and commands for Node.js.
import { ExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

const tableName = process.argv[2];
const movieYear1 = process.argv[3];
const movieTitle1 = process.argv[4];

export const run = async (tableName, movieYear1, movieTitle1) => {
  const params = {
    Statement: "DELETE FROM " + tableName + " where title=? and year=?",
    Parameters: [{ S: movieTitle1 }, { N: movieYear1 }],
  };
  try {
    const data = await ddbDocClient.send(new ExecuteStatementCommand(params));
    console.log("Success. Item deleted.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(tableName, movieYear1, movieTitle1);
// snippet-end:[dynamodb.JavaScript.partiQL.deleteItemV3]
