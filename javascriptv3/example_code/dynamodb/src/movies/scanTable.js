/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
scanTable.js demonstrates how to return items and attributes from an Amazon DynamoDB table.


Inputs (replace in code):
- TABLE_NAME
- MOVIE_RANK
- MOVIE_NAME
- MOVIE_YEAR

Running the code:
node scanTable.js
*/
// snippet-start:[dynamodb.JavaScript.movies.scanV3]

// Import required AWS SDK clients and commands for Node.js.
import { ddbDocClient } from "../libs/ddbDocClient.js";
import { ScanCommand } from "@aws-sdk/lib-dynamodb";
// Set the parameters.
export const params = {
  TableName: "TABLE_NAME",
  ProjectionExpression: "#r, #y, title",
  ExpressionAttributeNames: { "#r": "rank", "#y": "year" },
  FilterExpression: "title = :t and #y = :y and info.#r = :r",
  ExpressionAttributeValues: {
    ":r": "MOVIE_RANK",
    ":y": "MOVIE_YEAR",
    ":t": "MOVIE_NAME",
  },
};

export const scanTable = async () => {
  try {
    const data = await ddbDocClient.send(new ScanCommand(params));
    console.log("success", data.Items);
  } catch (err) {
    console.log("Error", err);
  }
};
scanTable();
// snippet-end:[dynamodb.JavaScript.movies.scanV3]
