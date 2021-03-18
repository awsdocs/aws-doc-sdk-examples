/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Purpose:
mylambdafunction.ts is an AWS Lambda function.It is part of a tutorial demonstrates
how to create a REST API using API Gateway that triggers a Lambda function that scans an
Amazon DynamoDB table of employees' information and send an Amazon Simple Notification Service (Amazon SNS)
message based on the results. It demonstrates how toTo run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Inputs (replace in code):
- REGION
- TABLE_NAME

*/
// snippet-start:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3]
// snippet-start:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3.config]

"use strict";
// Load the required clients and commands.
const { DynamoDBClient, PutRecord } = require("@aws-sdk/client-dynamodb");

//Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters for the ScanCommand method.
const params = {
  // Specify which items in the results are returned.
  FilterExpression: "startDate = :topic",
  // Define the expression attribute value, which are substitutes for the values you want to compare.
  ExpressionAttributeValues: {
    ":topic": { S: date },
  },
  // Set the projection expression, which the the attributes that you want.
  ProjectionExpression: "firstName, phone",
  TableName: "TABLE_NAME",
};

// Create the client service objects.
const dbclient = new DynamoDBClient({ region: REGION });

// snippet-end:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3.config]
// snippet-start:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3.handler]
exports.handler = async (event, context, callback) => {
  // Helper function to send message using Amazon SNS.
  async function sendText(textParams) {
    try {
      const data = await snsclient.send(new PublishCommand(textParams));
      console.log("Message sent");
    } catch (err) {
      console.log("Error, message not sent ", err);
    }
  }
  try {
    // Scan the table to check identify employees with work anniversary today.
    const data = await dbclient.send(new ScanCommand(params));
    data.Items.forEach(function (element, index, array) {
      const textParams = {
        PhoneNumber: element.phone.N,
        Message:
          "Hi " +
          element.firstName.S +
          "; congratulations on your work anniversary!",
      };
      // Send message using Amazon SNS.
      sendText(textParams);
    });
  } catch (err) {
    console.log("Error, could not scan table ", err);
  }
};
// snippet-end:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3.handler]
// snippet-end:[lambda.JavaScript.general-examples-dynamodb-lambda.scanAndPublishV3]
