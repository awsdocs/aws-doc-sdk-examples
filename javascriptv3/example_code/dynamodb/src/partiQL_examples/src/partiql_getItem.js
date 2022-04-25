/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
partiql_getItem.js demonstrates how to retrieve an item from an Amazon DynamoDB table using PartiQL.

Running the code:
node partiQL_getItem.js <tableName> <movieTitle1>

For example, node partiQL_getItem.js Movies 'The Departed'

*/
// snippet-start:[dynamodb.JavaScript.partiQL.getItemV3]

// Import required AWS SDK clients and commands for Node.js.
import { ExecuteStatementCommand } from "@aws-sdk/client-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient";

const tableName = process.argv[2];
const movieTitle1 = process.argv[3];

export const run = async (tableName, movieTitle1) => {
  const params = {
    Statement: "SELECT * FROM " + tableName + " where title=?",
    Parameters: [{ S: movieTitle1 }],
  };
  try {
    const data = await ddbDocClient.send(new ExecuteStatementCommand(params));
    for (let i = 0; i < data.Items.length; i++) {
      console.log(
        "Success. The query return the following data. Item " + i,
        data.Items[i].year,
        data.Items[i].title,
        data.Items[i].info
      );
    }
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.error(err);
  }
};
run(tableName, movieTitle1);
// snippet-end:[dynamodb.JavaScript.partiQL.getItemV3]
