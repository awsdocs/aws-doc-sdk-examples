/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-example-query-scan.html.

Purpose:
ddb_query.js demonstrates how to find items in an Amazon DynamoDB table.

Inputs (replace in code):
- REGION

Running the code:
node ddb_query.js
*/
// snippet-start:[dynamodb.JavaScript.table.queryV3]

// Import required AWS SDK clients and commands for Node.js
const {DynamoDBClient,
  QueryCommand} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
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

// Create DynamoDB service object
const dbclient = new DynamoDBClient(REGION);
const run = async () => {
  try {
    const results = await dbclient.send(new QueryCommand(params));
    results.Items.forEach(function(element, index, array) {
      console.log(element.Title.S + " (" + element.Subtitle.S + ")");
    });
  }
  catch (err) {
    console.error(err);
  }
};
run();
// snippet-end:[dynamodb.JavaScript.table.queryV3]
exports.run = run; //for unit tests only

