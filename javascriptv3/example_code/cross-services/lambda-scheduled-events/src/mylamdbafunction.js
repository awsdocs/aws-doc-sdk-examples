/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Purpose:
mylambdafunction.ts is an AWS Lambda function. It is part of a tutorial demonstrates how to execute Lambda functions
 using Amazon CloudWatch scheduled events. To see the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Inputs (replace in code):
- REGION
- TABLE_NAME

*/
// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.scanAndPublishV3]
// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.config]

"use strict";
const { ScanCommand } = require("@aws-sdk/client-dynamodb");
const { PublishCommand } = require("@aws-sdk/client-sns");
const {snsClient} = require ( "./libs/snsClient" );
const {dynamoClient} = require ( "./libs/dynamoClient" );

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
  TableName: "Employees",
};

// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.config]
// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.scanAndPublishV3.handler]
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
    const data = await dynamoClient.send(new ScanCommand(params));
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
// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.scanAndPublishV3.handler]
// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.scanAndPublishV3]
