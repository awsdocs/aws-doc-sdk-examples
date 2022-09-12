/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-function-prep.html.

Purpose:
slotpull.ts runs the lambda function for this example.

Inputs (into code):
- REGION
- TABLE_NAME

Running the code:
ts-node lambda-function-setup.ts
*/
"use strict";

// Load the DynamoDB client
const { DynamoDBClient, GetItemCommand } = require("@aws-sdk/client-dynamodb");

//Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({ region: REGION });

// Set the parameters
const tableName = "TABLE_NAME";

exports.handler = (_event, _context, callback) => {
  // Define the object that will hold the data values returned
  const slotResults = {
    isWinner: false,
    leftWheelImage: { file: { S: "" } },
    middleWheelImage: { file: { S: "" } },
    rightWheelImage: { file: { S: "" } },
  };

  // =============================LEFT===========================================
  // Set a random number 0-9 for the left slot position
  const leftParams = {
    TableName: tableName,
    Key: { slotPosition: { N: Math.floor(Math.random() * 10).toString() } },
  };
  // Call DynamoDB to retrieve the image to use for the left slot result
  const myLeftPromise = ddb.send(new GetItemCommand(leftParams)).then(
    (data) => data.Item.imageFile.S,
    () => {
      console.log("Database read error on left wheel.");
    }
  );

  // =============================MIDDLE===========================================
  // Set a random number 0-9 for the middle slot position
  const middleParams = {
    TableName: tableName,
    Key: { slotPosition: { N: Math.floor(Math.random() * 10).toString() } },
  };
  // Call DynamoDB to retrieve the image to use for the left slot result
  const myMiddlePromise = ddb.send(new GetItemCommand(middleParams)).then(
    (data) => data.Item.imageFile.S,
    () => {
      console.log("Database read error on middle wheel.");
    }
  );

  // =============================RIGHT===========================================
  // Set a random number 0-9 for the slot position
  const rightParams = {
    TableName: tableName,
    Key: { slotPosition: { N: Math.floor(Math.random() * 10).toString() } },
  };
  // Call DynamoDB to retrieve the image to use for the left slot result
  const myRightPromise = ddb.send(new GetItemCommand(rightParams)).then(
    (data) => data.Item.imageFile.S,
    () => {
      console.log("Database read error on right wheel.");
    }
  );

  Promise.all([myLeftPromise, myMiddlePromise, myRightPromise]).then(function (
    values
  ) {
    slotResults.leftWheelImage.file.S = values[0];
    slotResults.middleWheelImage.file.S = values[1];
    slotResults.rightWheelImage.file.S = values[2];
    // If all three values are identical, the spin is a winner
    if (values[0] === values[1] && values[0] === values[2]) {
      slotResults.isWinner = true;
    }
    // Return the JSON result to the caller of the Lambda function
    callback(null, slotResults);
  });
};
