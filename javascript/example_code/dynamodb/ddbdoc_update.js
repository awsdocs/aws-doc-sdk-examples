/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

//snippet-sourcedescription:[ddbdoc_update.js demonstrates how to use a DocumentClient to create or update an item in an Amazon DynamoDB table.]
//snippet-service:[dynamodb]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/dynamodb-example-document-client.html

// snippet-start:[dynamodb.JavaScript.docClient.update]
// Load the AWS SDK for Node.js
const AWS = require('aws-sdk');
// Set the region
AWS.config.update({region: 'REGION'});

// Create DynamoDB document client
const docClient = new AWS.DynamoDB.DocumentClient({apiVersion: '2012-08-10'});

// Create constiables to hold numeric key values
const season = SEASON_NUMBER;
const episode = EPISODES_NUMBER;

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

(async () => {
  try {
    const data = await docClient.update(params).promise();
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
})();
// snippet-end:[dynamodb.JavaScript.docClient.update]
