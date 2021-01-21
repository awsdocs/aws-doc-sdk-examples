/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
mylambdafunction.ts demonstrates how to create a Amazon Lambda function that creates an Amazon DynamoDB database table.
It is part of a tutorial demonstrating how to build and deploy an app that transcribes and displays
voice recordings for authenticated users. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
For more information, see https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.
*/
// snippet-start:[lambda.JavaScript.general-examples.CreateTableV3]
// Load the required clients and packages
"use strict";

// Load the DynamoDB client
const { DynamoDBClient, CreateTableCommand } = require("@aws-sdk/client-dynamodb");

//Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

const params = {
    AttributeDefinitions: [
        {
            AttributeName: "Season", //ATTRIBUTE_NAME_1
            AttributeType: "N", //ATTRIBUTE_TYPE
        },
        {
            AttributeName: "Episode", //ATTRIBUTE_NAME_2
            AttributeType: "N", //ATTRIBUTE_TYPE
        },
    ],
    KeySchema: [
        {
            AttributeName: "Season", //ATTRIBUTE_NAME_1
            KeyType: "HASH",
        },
        {
            AttributeName: "Episode", //ATTRIBUTE_NAME_2
            KeyType: "RANGE",
        },
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1,
    },
    TableName: "TABLE_NAME", //TABLE_NAME
    StreamSpecification: {
        StreamEnabled: false,
    },
};

// Instantiate a DynamoDB client
const ddb = new DynamoDBClient({ region: REGION });

exports.handler = async(event, context, callback) => {
    try {
        const data = await ddb.send(new CreateTableCommand(params));
        console.log("Table Created", data);
    } catch (err) {
        console.log("Error", err);
    }
};
// snippet-end:[lambda.JavaScript.general-examples.CreateTableV3]

