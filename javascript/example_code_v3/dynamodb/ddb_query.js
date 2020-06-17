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
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-query-scan.html

Purpose:
ddb_query.js demonstrates how to find items in an Amazon DynamoDB table.

Inputs:
- REGION (in command line input below)

Running the code:
node ddb_query.js REGION
*/
// snippet-start:[dynamodb.JavaScript.table.query]
async function run() {
  try {
    const params = {
      KeyConditionExpression: 'Season = :s and Episode > :e',
      FilterExpression: 'contains (Subtitle, :topic)',
      ExpressionAttributeValues: {
        ':s' : {N: '1'},
        ':e' : {N: '2'},
        ':topic' : {S: 'SubTitle'}
      },
      ProjectionExpression: 'Episode, Title, Subtitle',
      TableName: 'EPISODES_TABLE'
    };
    const {DynamoDBClient,
      QueryCommand} = require("@aws-sdk/client-dynamodb");
    const region = process.argv[2];
    const dbclient = new DynamoDBClient({region: region});
    const results = await client.send(new QueryCommand(params));
    results.Items.forEach(function(element, index, array) {
      console.log(element.Title.S + " (" + element.Subtitle.S + ")");
    });
  }
  catch (err) {
    console.error(err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.query]
exports.run = run;
const params = {
  KeyConditionExpression: 'Season = :s and Episode > :e',
  FilterExpression: 'contains (Subtitle, :topic)',
  ExpressionAttributeValues: {
    ':s' : {N: '1'},
    ':e' : {N: '2'},
    ':topic' : {S: 'SubTitle'}
  },
  ProjectionExpression: 'Episode, Title, Subtitle',
  TableName: 'EPISODES_TABLE'
};
exports.params = params;

