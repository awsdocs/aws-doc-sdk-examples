/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
/* ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_getitem.js demonstrates how to retrieve the attributes of an item from an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)
- KEY_NAME (into code; the primary key of the table, e.g., 'CUSTOMER_ID')
- KEY_NAME_VALUE (into code; the value of the primary key row containing the attribute value)
- ATTRIBUTE_NAME (into code; the name of the attribute column containing the attribute value)

Running the code:
node ddb_getitem.js REGION TABLE
*/
// snippet-start:[dynamodb.JavaScript.v3.item.getItem]
// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, GetItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS Region
const region = process.argv[2];
// Create DynamoDB service object
const {DocumentClient} = require("@aws-sdk/client-docdb")
// Set the parameters
const params = {
  TableName: 'TABLE_NAME',
  Key: {
    'KEY_NAME' : {N: 'KEY_VALUE'}
  },
  ProjectionExpression: 'ATTRIBUTE_NAME'
};

async function run(){
  const dbclient = new DynamoDBClient(region);
  const data = await dbclient.send(new GetItemCommand(params));
  console.log("Success", data.Item);
};
run();
// snippet-end:[dynamodb.JavaScript.v3.item.getItem]
exports.run = run; //for unit tests only

