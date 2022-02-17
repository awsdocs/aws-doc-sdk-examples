/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-scan-and-publish-message.html.

Purpose:
populate-table.js demonstrates how to populate an Amazon DynamoDB table.
It is part of a tutorial that demonstrates how to create a REST API using API Gateway that triggers a Lambda function that scans an
Amazon DynamoDB table of employees' information and send an Amazon Simple Notification Service (Amazon SNS)
message based on the results.

Running the code:
node populate-table.js

*/
// snippet-start:[lambda.JavaScript.general-examples-dynamodb-lambda.CreateTableV3]
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
            phone: { N: "155555555555657" },
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
// snippet-end:[lambda.JavaScript.general-examples-dynamodb-lambda.CreateTableV3]
