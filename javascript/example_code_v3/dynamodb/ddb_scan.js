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
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html

Inputs:
- REGION (in command line input below)

Purpose:
ddb_scan.js demonstrates how to return items and attributes from an Amazon DynamoDB table.

Running the code:
node ddb_scan.js REGION
*/
// snippet-start:[dynamodb.JavaScript.table.scan]
// Load the AWS SDK for Node.js
async function run() {
  try {
    const params = {
      KeyConditionExpression: 'Season = :t and Subtitle = :topic',
      FilterExpression: 'contains (Subtitle, :topic)',
      ExpressionAttributeValues: {
        ':t' : {S: 'Title1'},
        ':topic': {S: 'Sub'}
      },
      ProjectionExpression: 'Season, Episode, Title, Subtitle',
      TableName: 'EPISODES_TABLE'
    };
    const {DynamoDBClient,
      ScanCommand} = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const dbclient = new DynamoDBClient({region: region});
    const data = await dbclient.send(new ScanCommand(params));
    data.Items.forEach(function(element, index, array) {
      console.log(element.Title.S + " (" + element.Subtitle.S + ")");
    });
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.scan]
exports.run = run;
const params = {
  KeyConditionExpression: 'Season = :t and Subtitle = :topic',
  FilterExpression: 'contains (Subtitle, :topic)',
  ExpressionAttributeValues: {
    ':t' : {S: 'Title1'},
    ':topic': {S: 'Sub'}
  },
  ProjectionExpression: 'Season, Episode, Title, Subtitle',
  TableName: 'EPISODES_TABLE'
};
exports.params = params;
