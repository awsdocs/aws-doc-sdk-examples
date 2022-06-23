
/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Purpose:
populate-table.ts demonstrates how to populate an Amazon DynamoDB table.
It is part of a tutorial that demonstrates how to run Lambda functions using Amazon CloudWatch scheduled events. To see the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/invoke-lambda-functions-with-scheduled-events.html.

Inputs (replace in code):
- REGION

Running the code:
ts-node populate-table.t s

*/
// snippet-start:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.CreateTableV3]
// Load the required Amazon DynamoDB client and commands.
const {
  BatchWriteItemCommand
} = require("@aws-sdk/client-dynamodb");
const {dynamoClient} = require ( "../libs/dynamoClient.js" );

// Set the parameters.
const params = {
  RequestItems: {
    Employees: [
      {
        PutRequest: {
          Item: {
            id: { N: "1" },
            firstName: { S: "Bob" },
            phone: { N: "155555555555653" },
            startDate: { S: "2020-6-17" },
          },
        },
      },
      {
        PutRequest: {
          Item: {
            id: { N: "2" },
            firstName: { S: "Xing" },
            phone: { N: "155555555555653" },
            startDate: { S: "2019-12-17" },
          },
        },
      },
      {
        PutRequest: {
          Item: {
            id: { N: "55" },
            firstName: { S: "Harriette" },
            phone: { N: "155555555555652" },
            startDate: { S: "2019-12-19" },
          },
        },
      },
    ],
  },
};

const run = async () => {
  try {
    const data = await dynamoClient.send(new BatchWriteItemCommand(params));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[lambda.JavaScript.cross-service-examples.lambda-scheduled-events.CreateTableV3]
