/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write-batch.html.

Purpose:
ddb_batchgetitem.ts demonstrates how to retrieve items from an Amazon DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE
- KEY_NAME
- KEY_VALUE
- ATTRIBUTE_NAME

Running the code:
ts-node ddb_batchgetitem.ts
*/
// snippet-start:[dynamodb.JavaScript.batch.GetItemV3]
// Import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  BatchGetItemCommand
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  RequestItems: {
    TABLE_NAME: {
      Keys: [
        {
          KEY_NAME: { N: "KEY_VALUE" },
          KEY_NAME: { N: "KEY_VALUE" },
          KEY_NAME: { N: "KEY_VALUE" },
        },
      ],
      ProjectionExpression: "ATTRIBUTE_NAME",
    },
  },
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient({ region: REGION });

const run = async () => {
  try {
    const data = await dbclient.send(new BatchGetItemCommand(params));
    console.log("Success, items retrieved", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.GetItemV3]

