/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
/* ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-table-read-write.html

Purpose:
ddb_putitem.js demonstrates how to create or update an item in an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)

Running the code:
node ddb_putitem.js REGION
*/
// snippet-start:[dynamodb.JavaScript.item.putItem]
async function run(){
  try {
    const {
      DynamoDBClient,
      PutItemCommand
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const params = {
      TableName: 'CUSTOMER_LIST_TEST_ANOTHER',
      Item: {
        'CUSTOMER_ID': {N: '002'},
        'CUSTOMER_NAME': {S: 'Richard Rue'}
      }
    };
    const dbclient = new DynamoDBClient({region: region});
    const data = await dbclient.send(new PutItemCommand(params));
    console.log(results);
  }
  catch (err) {
    console.error(err);
  }
};
run();

// snippet-end:[dynamodb.JavaScript.item.putItem]
const params = {
  TableName: 'CUSTOMER_LIST_TEST_ANOTHER',
  Item: {
    'CUSTOMER_ID': {N: '002'},
    'CUSTOMER_NAME': {S: 'Richard Rue'}
  }
};
exports.run = run;
