/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/api-gateway-invoking-lambda-example.html.

Purpose:
dynamoClient.js is a helper function that creates an Amazon DynamoDB service client.

Inputs (replace in code):
-REGION

*/
// snippet-start:[dynamodb.JavaScript.apigateway.createclientv3]

const { DynamoDBClient } = require ( "@aws-sdk/client-dynamodb" );
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon DynamoDB service client object.
const dynamoClient = new DynamoDBClient({ region: REGION });
module.exports = { dynamoClient };
// snippet-end:[dynamodb.JavaScript.apigateway.createclientv3]


