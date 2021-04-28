/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_scan.ts demonstrates how to return items and attributes from an Amazon DynamoDB table.

Inputs (replace in code):
- REGION

Running the code:
ts-node ddb_scan.ts
*/
// snippet-start:[dynamodb.JavaScript.table.scanV3]

// Import required AWS SDK clients and commands for Node.js
const { DynamoDBClient, ScanCommand } = require("@aws-sdk/client-dynamodb");
const REGION = "REGION";

// Set the parameters.
const params = {
  // Specify which items in the results are returned.
  FilterExpression: "Subtitle = :topic AND Season = :s AND Episode = :e",
  // Define the expression attribute value, which are substitutes for the values you want to compare.
  ExpressionAttributeValues: {
    ":topic": { S: "SubTitle2" },
    ":s": { N: "1" },
    ":e": { N: "2" }
  },
  // Set the projection expression, which the the attributes that you want.
  ProjectionExpression: "Season, Episode, Title, Subtitle",
  TableName: "EPISODES_TABLE",
};

// Create DynamoDB service object.
const dbclient = new DynamoDBClient({ region: REGION });

async function run() {
  try {

    const data = await dbclient.send(new ScanCommand(params));
    data.Items.forEach(function (element, index, array) {
      console.log(element.Title.S + " (" + element.Subtitle.S + ")");
    });
  } catch (err) {
    console.log("Error", err);
  }
}
run();
// snippet-end:[dynamodb.JavaScript.table.scanV3]

