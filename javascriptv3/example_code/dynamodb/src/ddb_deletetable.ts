/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples-using-tables.html.

Purpose:
ddb_deletetable.ts demonstrates how to delete an Amazon DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
ts-node ddb_deletetable.ts

*/
// snippet-start:[dynamodb.JavaScript.item.deleteTableV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  DeleteTableCommand
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  TableName: "TABLE_NAME",
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient({ region: REGION });

const run = async () => {
  try {
    const data = await dbclient.send(new DeleteTableCommand(params));
    console.log("Success, table deleted", data);
  } catch (err) {
    if (err && err.code === "ResourceNotFoundException") {
      console.log("Error: Table not found");
    } else if (err && err.code === "ResourceInUseException") {
      console.log("Error: Table in use");
    }
  }
};
run();
// snippet-end:[dynamodb.JavaScript.item.deleteTableV3]

