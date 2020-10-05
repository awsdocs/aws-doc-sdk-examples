/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_getitem.ts demonstrates how to retrieve the attributes of an item from an AWS DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME
- KEY_NAME: the primary key of the table, e.g., 'CUSTOMER_ID'
- KEY_NAME_VALUE: the value of the primary key row containing the attribute value
- ATTRIBUTE_NAME: the name of the attribute column containing the attribute value

Running the code:
ts-node ddb_getitem.ts
*/
// snippet-start:[dynamodb.JavaScript.item.getItemV3]
// Import required AWS SDK clients and commands for Node.js
const { DynamoDBClient, GetItemCommand } = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  TableName: "TABLE_NAME", //TABLE_NAME
  Key: {
    KEY_NAME: { N: "KEY_VALUE" },
  },
  ProjectionExpression: "ATTRIBUTE_NAME",
};

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);

const run = async () => {
  const data = await dbclient.send(new GetItemCommand(params));
  console.log("Success", data.Item);
};
run();
// snippet-end:[dynamodb.JavaScript.item.getItemV3]

