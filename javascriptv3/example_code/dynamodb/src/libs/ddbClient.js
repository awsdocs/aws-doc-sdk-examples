/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/dynamodb-examples.html.

Purpose:
ddbClient.js is a helper function that creates an Amazon DynamoDB service client.

Inputs (replace in code):
- REGION

*/
// snippet-start:[dynamodb.JavaScript.tables.createclientv3]
// Create service client module using ES6 syntax.
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"
// Create an Amazon S3 service client object.
const ddbClient = new DynamoDBClient({ region: REGION });
export { ddbClient };
// snippet-end:[dynamodb.JavaScript.tables.createclientv3]


