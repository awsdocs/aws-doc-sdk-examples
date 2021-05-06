/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-dynamodb-utilities.html.

Purpose:
ddbdoc_get_item.ts demonstrates how to use Amazon DynamoDB utilities to retrieve a set of attributes for an item in an Amazon DynamoDB table..

Inputs (replace in code):
- TABLE_NAME
- REGION
- primaryKey
- sortKey (only required if table has sort key)
- VALUE_1: Value for the primary key (The format for the datatype must match the schema. For example, if the primaryKey is a number, VALUE_1 has no inverted commas.)
- VALUE_2: Value for the primary key (The format for the datatype must match the schema.)

Running the code:
ts-node ddbdoc_get_item.ts
*/
// snippet-start:[dynamodb.JavaScript.docClient.getV3]

// Import the required AWS SDK clients and command for Node.js
const { DynamoDBClient, GetItemCommand } = require("@aws-sdk/client-dynamodb");
const { marshall, unmarshall } = require("@aws-sdk/util-dynamodb");

// Set the parameters
const params = {
  TableName: "TABLE_NAME",
  /*
  Convert the key JavaScript object you are retrieving to the
  required Amazon DynamoDB record. The format of values specifies
  the datatype. The following list demonstrates different
  datatype formatting requirements:
  String: "String",
  NumAttribute: 1,
  BoolAttribute: true,
  ListAttribute: [1, "two", false],
  MapAttribute: { foo: "bar" },
  NullAttribute: null
   */
  Key: marshall({
    primaryKey: VALUE, // For example, "Season: 2"
    sortKey: VALUE, // For example,  "Episode: 1" (only required if table has sort key)
  }),
};

// Create DynamoDB document client
const client = new DynamoDB({ region: "REGION" });

const run = async () => {
  try {
    const { Item } = await client.getItem(params);
// Convert the DynamoDB record you retrieved into a JavaScript object
    const jsObject = unmarshall(Item);
// Print the JavaScript object to console
    console.log(jsObject);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.getV3]
