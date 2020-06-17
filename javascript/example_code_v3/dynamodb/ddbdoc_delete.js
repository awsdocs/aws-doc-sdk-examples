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
ddbdoc_delete.js demonstrates how to use a DocumentClient to delete an item from an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)
- TABLE_NAME (in code)
- HASH_KEY (in code)
- VALUE (in code)

Running the code:
node ddbdoc_delete.js REGION
*/
// snippet-start:[dynamodb.JavaScript.docClient.delete]
// Load the AWS SDK for Node.js
async function run() {
  try {
    const params = {
      TableName: "TABLE_NAME",
      Key: {
        'HASH_KEY': 'VALUE'
      }
    };
    const {
      DynamoDBClient, DeleteItemCommand, DocumentClient
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const dbclient = new DynamoDBClient.DocumentClient({region: region});
    const data = await dbclient.send(new DeleteItemCommand(params));
    console.log("Success, item deleted", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.delete]
exports.run = run;
