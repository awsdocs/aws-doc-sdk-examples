/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_deleteitem.ts demonstrates how to delete an item from an Amazon DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE
- KEY_NAME
- VALUE

Running the code:
ts-node ddb_deleteitem.ts

*/
// snippet-start:[dynamodb.JavaScript.item.deleteItemV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  DeleteItemCommand
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
var params = {
  TableName: 'TABLE_NAME',
  Key: {
    'KEY_NAME': {N: 'VALUE'}
  }
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

const run = async () => {
  try {
    const data = await dbclient.send(new DeleteItemCommand(params));
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
// snippet-end:[dynamodb.JavaScript.item.deleteItemV3]

