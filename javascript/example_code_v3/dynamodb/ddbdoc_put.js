/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE:This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-document-client.html

Purpose:
ddbdoc_put.js demonstrates how to ues a DocumentClient to create or replace an item in an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)
- TABLE (in code)
- HASH_KEY (in code)
- ATTRIBUTE_# (in code)
- STRING_VALUE (in code)

Running the code:
node ddbdoc_put.js REGION
*/
// snippet-start:[dynamodb.JavaScript.docClient.put]
async function run() {
  try {
    const {
      DynamoDBClient, PutItemCommand, DocumentClient
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const dbclient = new DynamoDBClient.DocumentClient({region: region});
    var params = {
      TableName: 'TABLE',
      Item: {
        'HASH_KEY': VALUE,
        'ATTRIBUTE_1': 'STRING_VALUE_1',
        'ATTRIBUTE_2': 'STRING_VALUE_2'
      }
    };
    const data = await dbclient.send(new PutItemCommand(params));
    console.log("Success, item retrieved", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.put]
exports.run = run;
