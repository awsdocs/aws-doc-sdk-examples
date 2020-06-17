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
ddbdoc_update.js demonstrates how to use a DocumentClient to create or update an item in an Amazon DynamoDB table.

Inputs:
- REGION  (in command line input below)

Running the code:
node ddbdoc_update.js REGION
*/
// snippet-start:[dynamodb.JavaScript.docClient.update]
async function run() {
  try {
    const {
      DynamoDBClient, UpdateTableCommand, DocumentClient
    } = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const season = 'SEASON_NUMBER';
    const episode = 'EPISODES_NUMBER';
    const params = {
      TableName: 'EPISODES_TABLE',
      Key: {
        'Season' : season,
        'Episode' : episode
      },
      UpdateExpression: 'set Title = :t, Subtitle = :s',
      ExpressionAttributeValues: {
        ':t' : 'NEW_TITLE',
        ':s' : 'NEW_SUBTITLE'
      }
    };
    const dbclient = new DynamoDBClient.DocumentClient({region: region});
    const data = await dbclient.send(new UpdateTableCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.docClient.update]
exports.run = run;
