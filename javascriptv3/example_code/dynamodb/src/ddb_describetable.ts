/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples-using-tables.html.

Purpose:
ddb_describetable.ts demonstrates how to retrieve information about an Amazon DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
ts-node ddb_describetable.ts
*/
// snippet-start:[dynamodb.JavaScript.table.describeTableV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  DescribeTableCommand,
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { TableName: "TABLE_NAME" }; //TABLE_NAME

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

const run = async () => {
  try {
    const data = await dbclient.send(new DescribeTableCommand(params));
    console.log("Success", data.Table.KeySchema);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.describeTableV3]

