/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
/* ABOUT THIS NODE.JS EXAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write.html.

Purpose:
ddb_putitem.js demonstrates how to create or update an item in an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TableName

Running the code:
node ddb_putitem.js REGION TABLE_NAME
*/
// snippet-start:[dynamodb.JavaScript.v3.item.putItem]
// Import required AWS-SDK clients and commands for Node.js
const {DynamoDBClient, PutItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {
  TableName: process.argv[3],
  Item: {
    'CUSTOMER_ID': {N: '001'},
    'CUSTOMER_NAME': {S: 'Richard Roe'}
  }
};

async function run(){
  try {

    const data = await dbclient.send(new PutItemCommand(params));
    console.log(data);
  }
  catch (err) {
    console.error(err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.item.putItem]
exports.run = run; //for unit tests only
