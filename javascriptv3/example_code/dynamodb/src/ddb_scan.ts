/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_scan.ts demonstrates how to return items and attributes from an AWS DynamoDB table.

Inputs (replace in code):
- REGION

Running the code:
ts-node ddb_scan.ts
*/
// snippet-start:[dynamodb.JavaScript.table.scanV3]

// Import required AWS SDK clients and commands for Node.js
const { DynamoDBClient, ScanCommand } = require("@aws-sdk/client-dynamodb");
const REGION = "REGION";

// Set parameters
const params = {
  KeyConditionExpression: "Subtitle = :topic",
  FilterExpression: "contains (Subtitle, :topic)",
  ExpressionAttributeValues: {
    ":topic": { S: "Sub" },
  },
  ProjectionExpression: "Season, Episode, Title, Subtitle",
  TableName: "EPISODES_TABLE",
};

// Create DynamoDB service object
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

