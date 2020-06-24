/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
/* ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-examples-using-tables.html.

Purpose:
ddb_describetable.js demonstrates how to retrieve information about an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)

Running the code:
node ddb_describetable.js REGION TABLE_NAME
*/
// snippet-start:[dynamodb.JavaScript.v3.table.describeTable]
// Import required AWS-SDK clients and commands for Node.js
const {DynamoDBClient, DescribeTableCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS region
const region= process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {TableName: process.argv[3]};

async function run() {
  try {
    const data = await dbclient.send(new DescribeTableCommand(params));
    console.log("Success", data.Table.KeySchema);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.table.describeTable]
exports.run = run; //for unit tests only
