/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_batch_putItems.js demonstrates how to insert items by batch into an Amazon DynamoDB table using PartiQL.


Running the code:
node partiql_batch_putItems.js <tableName> <movieYear1> <movieTitle1> <movieYear2> <movieTitle2>

For example, node partiql_batch_putItems.js Movies_batch 2006 'The Departed' 2013 '2 Guns'
*/
// snippet-start:[dynamodb.JavaScript.partiQL.putItemsV3]

// Import required AWS SDK clients and commands for Node.js.
import { BatchExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

const tableName = process.argv[2];
const movieYear1 = process.argv[3];
const movieTitle1 = process.argv[4];
const movieYear2 = process.argv[5];
const movieTitle2 = process.argv[6];

export const run = async (
  tableName,
  movieYear1,
  movieTitle1,
  movieYear2,
  movieTitle2
) => {
  const params = {
    Statements: [
      {
        Statement:
          "INSERT INTO " + tableName + "  value  {'title':?, 'year':?}",
        Parameters: [{ S: movieTitle1 }, { N: movieYear1 }],
      },
      {
        Statement:
          "INSERT INTO " + tableName + "  value  {'title':?, 'year':?}",
        Parameters: [{ S: movieTitle2 }, { N: movieYear2 }],
      },
    ],
  };
  try {
    const data = await ddbDocClient.send(
      new BatchExecuteStatementCommand(params)
    );
    console.log("Success. Items added.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(tableName, movieYear1, movieTitle1, movieYear2, movieTitle2);
// snippet-end:[dynamodb.JavaScript.partiQL.putItemsV3]
