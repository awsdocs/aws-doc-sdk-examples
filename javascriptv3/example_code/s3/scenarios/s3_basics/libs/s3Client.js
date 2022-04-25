/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
User Guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
s3Client.js is a helper function that creates an Amazon Simple Storage Service (Amazon S3) service client.

Inputs (replace in code):

*/
// snippet-start:[s3_basics.JavaScript.createclientv3]
// Create a service client module using ES6 syntax.
import { S3Client } from "@aws-sdk/client-s3";
// Set the AWS Region.
export const REGION = "REGION"; //For example, "us-east-1".
// Create an Amazon S3 service client object.
export const s3Client = new S3Client({ region: REGION });
// snippet-end:[s3_basics.JavaScript.createclientv3]
