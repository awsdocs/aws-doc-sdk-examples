/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
mylambdafunction.ts demonstrates how to create a AWS Lambda function that creates an Amazon DynamoDB database table.
It is part of a tutorial demonstrating how create and deploy an AWS Lambda function. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

Inputs (replace in code):
- TABLE_NAME

*/
// snippet-start:[lambda.JavaScript.general-examples-lambda-create-function.CreateTableV3]
"use strict";
// Load the required clients and packages.
const { CreateTableCommand } = require ( "@aws-sdk/client-dynamodb" );
const { dynamoClient } = require (  "./libs/dynamoClient" );

// Set the parameters.
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

exports.handler = async(event, context, callback) => {
    try {
        const data = await dynamoClient.send(new CreateTableCommand(params));
        console.log("Table Created", data);
    } catch (err) {
        console.log("Error", err);
    }
};
// snippet-end:[lambda.JavaScript.general-examples-lambda-create-function.CreateTableV3]

