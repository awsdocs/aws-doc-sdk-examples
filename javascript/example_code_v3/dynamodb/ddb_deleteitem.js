/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_deleteitem.js demonstrates how to delete an item from an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)

Running the code:
node.js ddb_deleteitem.js REGION TABLE_NAME

*/
// snippet-start:[dynamodb.JavaScript.v3.item.deleteItem]
// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, DeleteItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS Region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {
  TableName: process.argv[3],
  Key: {
    'CUSTOMER_ID': {N: '1'},
    'CUSTOMER_NAME': {S: 'Richard Roe'}
  }
};

async function run() {
  try {
    const data = await dbclient.send(new DeleteItemCommand(params));
    console.log("Success, item deleted", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.item.deleteItem]
//for unit tests only
exports.run = run; //for unit tests only

