/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiQL_updateItem.js demonstrates how to update an item in an Amazon DynamoDB table using PartiQL.

Running the code:
node partiQL_updateItem.js  <tableName> <movieYear1> <movieTitle1> <producer1>

For example, node partiQL_updateItem.js Movies 2006 'The Departed' 'New View Films'
*/
// snippet-start:[dynamodb.JavaScript.partiQL.updateItemV3]

// Import required AWS SDK clients and commands for Node.js.
import { ExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

const tableName = process.argv[2];
const movieYear1 = process.argv[3];
const movieTitle1 = process.argv[4];
const producer1 = process.argv[5];

export const run = async (tableName, movieYear1, movieTitle1, producer1) => {
  const params = {
    Statement:
      "UPDATE " + tableName + "  SET Producer=? where title=? and year=?",
    Parameters: [{ S: producer1 }, { S: movieTitle1 }, { N: movieYear1 }],
  };
  try {
    await ddbDocClient.send(new ExecuteStatementCommand(params));
    console.log("Success. Item updated.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(tableName, movieYear1, movieTitle1, producer1);
// snippet-end:[dynamodb.JavaScript.partiQL.updateItemV3]
