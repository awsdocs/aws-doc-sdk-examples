/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write-batch.html.

Purpose:
ddb_batchwriteitem.js demonstrates how to put items or delete items into an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE_NAME (into command line below)
- KEYS (into code)
- KEY_VALUES (into code)
- ATTRIBUTE_VALUES (into code)

Running the code:
node ddb_batchwriteitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.v3.batch.WriteItem]
// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, BatchWriteItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS Region
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
