/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_batch_updateItems.js demonstrates how to update items by batch in an Amazon DynamoDB table using PartiQL.


Running the code:
node partiql_batch_updateItems.js  <tableName> <movieYear1> <movieTitle1> <movieYear1> <movieTitle1> <producer1> <producer2>

For example, node partiQL_basics.js Movies_batch 2006 'The Departed' 2013 '2 Guns' 'New View Films' 'Old Thyme Films'
*/
// snippet-start:[dynamodb.JavaScript.partiQL.updateItemsV3]

// Import required AWS SDK clients and commands for Node.js.
import { BatchExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient.js";

const tableName = process.argv[2];
const movieYear1 = process.argv[3];
const movieTitle1 = process.argv[4];
const movieYear2 = process.argv[6];
const movieTitle2 = process.argv[7];
const producer1 = process.argv[5];
const producer2 = process.argv[8];

export const run = async (
  tableName,
  movieYear1,
  movieTitle1,
  movieYear2,
  movieTitle2,
  producer1,
  producer2
) => {
  const params = {
    Statements: [
      {
        Statement:
          "UPDATE " + tableName + " SET producer=? where title=? and year=?",
        Parameters: [{ S: producer1 }, { S: movieTitle1 }, { N: movieYear1 }],
      },
      {
        Statement:
          "UPDATE " + tableName + " SET producer=? where title=? and year=?",
        Parameters: [{ S: producer2 }, { S: movieTitle2 }, { N: movieYear2 }],
      },
    ],
  };
  try {
    const data = await ddbDocClient.send(
      new BatchExecuteStatementCommand(params)
    );
    console.log("Success. Items updated.");
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(
  tableName,
  movieYear1,
  movieTitle1,
  movieYear2,
  movieTitle2,
  producer1,
  producer2
);
// snippet-end:[dynamodb.JavaScript.partiQL.updateItemsV3]
