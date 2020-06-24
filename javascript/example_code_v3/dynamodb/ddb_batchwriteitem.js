/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write-batch.html.

Purpose:
ddb_batchwriteitem.js demonstrates how to put or delete items into an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- KEY (into code; the key of the table)
- KEY_VALUE (into code; the value of the key to which you're adding the attributes)
- ATTRIBUTE_1_VALUE (into code; the name of the first attribute to add)
- ATTRIBUTE_2_VALUE (into code; the value of the key to identify the row to add the new attributes values to)
Running the code:
node ddb_batchwriteitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.v3.batch.WriteItem]
// Import required AWS-SDK clients and commands for Node.js
const {DynamoDBClient, BatchWriteItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {
  RequestItems: {
    "TABLE_NAME": [
      {
        PutRequest: {
          Item: {
            "KEY": {"N": "KEY_VALUE"},
            "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
            "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
          }
        }
      },
      {
        PutRequest: {
          Item: {
            "KEY": {"N": "KEY_VALUE"},
            "ATTRIBUTE_1": {"S": "ATTRIBUTE_1_VALUE"},
            "ATTRIBUTE_2": {"N": "ATTRIBUTE_2_VALUE"}
          }
        }
      }
    ]
  }
};

async function run() {
  try {
    const data = await dbclient.send(new BatchWriteItemCommand(params));
    console.log("Success, items inserted", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.v3.batch.WriteItem]
//for unit tests only
exports.run = run; //for unit tests only
