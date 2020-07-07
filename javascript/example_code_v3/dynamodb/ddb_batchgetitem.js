/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-table-read-write-batch.html.

Purpose:
ddb_batchgetitem.js demonstrates how to retrieve items from an Amazon DynamoDB table.

Inputs:
- REGION (into command line below)
- TABLE (into command line below)
- KEY_NAME (into code)
- KEY_VALUE (into code)
- ATTRIBUTE_NAME (into code)

Running the code:
node ddb_batchgetitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.batch.GetItemV3]
// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient, BatchGetItemCommand} = require("@aws-sdk/client-dynamodb");
// Set the AWS Region
const region = process.argv[2];
// Create DynamoDB service object
const dbclient = new DynamoDBClient(region);
// Set the parameters
const params = {
  RequestItems: {
    'TABLE_NAME': {
      Keys: [
        {
          'KEY_NAME': {N: 'KEY_VALUE'},
          'KEY_NAME': {N: "KEY_VALUE"},
          'KEY_NAME': {N: 'KEY_VALUE'}
        }
      ],
      ProjectionExpression: 'ATTRIBUTE_NAME'
    }
  }
};

async function run(){
  try {
    const data = await dbclient.send(new BatchGetItemCommand(params))
    console.log("Success, items retrieved", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.GetItemV3]
//for unit tests only
exports.run = run; //for unit tests only
