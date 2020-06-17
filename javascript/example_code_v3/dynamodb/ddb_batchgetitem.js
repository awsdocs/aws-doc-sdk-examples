/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

/* ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write-batch.html

Purpose:
ddb_batchgetitem.js demonstrates how to retrieve items from an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)
- TABLE (in code): The name of the DynamoDB table
- KEY_NAME(S)(in code)
- KEY_VALUE(S)(in code)
- ATTRIBUTE_NAME (in code)

Running the code:
node ddb_batchgetitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.batch.GetItem]
async function run(){
  try {
    const {
      DynamoDBClient, BatchGetItemCommand
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2]
    const params = {
      RequestItems: {
        'TABLE': {
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
    const dbclient = await new DynamoDBClient(region);
    const data = await dbclient.send(new BatchGetItemCommand(params))
    console.log("Success, items retrieved", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.batch.GetItem]
//for unit tests only
exports.run = run;
var params = {
  RequestItems: {
    'TABLE': {
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
exports.params = params;

