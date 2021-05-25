/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Purpose:
mylambdafunction.ts is an AWS Lambda function.It is part of a tutorial that demonstrates
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
// ES Modules import
import { DynamoDBClient, ScanCommand } from "@aws-sdk/client-dynamodb";
// CommonJS import
// const { DynamoDBClient, ScanCommand } = require("@aws-sdk/client-dynamodb");

// ES Modules import
import { SNSClient, PublishCommand } from "@aws-sdk/client-sns";
// CommonJS import
// const { SNSClient, PublishCommand } = require("@aws-sdk/client-sns");


const REGION = "REGION"; //e.g. "us-east-1"

// Get today's date.
const today = new Date();
const dd = String(today.getDate()).padStart(2, "0");
const mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
const yyyy = today.getFullYear();
const date = yyyy + "-" + mm + "-" + dd;

// Set the parameters for the ScanCommand method.
const params = {
  // Specify which items in the results are returned.
  FilterExpression: "startDate = :topic",
  // Define the expression attribute value, which are substitutes for the values you want to compare.
  ExpressionAttributeValues: {
    ":topic": { S: date },
  },
  // Set the projection expression, which are the attributes that you want.
  ProjectionExpression: "firstName, phone",
  TableName: "TABLE_NAME",
};

const dbclient = new DynamoDBClient({ region: REGION });
const snsclient = new SNSClient({ region: REGION });

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
    // Scan the table to identify employees with work anniversary today.
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
