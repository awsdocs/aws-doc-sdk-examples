/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3Client.js is a helper function that creates an Amazon Simple Storage Service (Amazon S3) service client.

Inputs (replace in code):

*/
// snippet-start:[dynamodb.JavaScript.partiQL.createclientv3]
// Create the DynamoDB service client module using ES6 syntax.
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
// Set the AWS Region.
export const REGION = "eu-west-1"; // For example, "us-east-1".
// Create an Amazon DynamoDB service client object.
export const ddbClient = new DynamoDBClient({ region: REGION });
// snippet-end:[dynamodb.JavaScript.partiQL.createclientv3]
