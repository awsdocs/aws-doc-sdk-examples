/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
create-table.js is part of a tutorial demonstrating how to build and deploy and app to submit
data to a DynamoDB table. create-table.js demonstrates how to create a DynamoDB table.

Inputs (replace in code):
- REGION
- TABLE_NAME

Running the code:
node create-table.js
 */
// snippet-start:[s3.JavaScript.crossservice.createTableV3]

// Import required AWS SDK clients and commands for Node.js
const {
    DynamoDB,
    CreateTableCommand, PutItemCommand
} = require("@aws-sdk/client-dynamodb");

// Set the AWS Region
const REGION = "eu-west-1"; //e.g. "us-east-1"

// Set the parameters
const tableParams = {
    AttributeDefinitions: [
        {
            AttributeName: "Id", //ATTRIBUTE_NAME_1
            AttributeType: "N", //ATTRIBUTE_TYPE
        }
    ],
    KeySchema: [
        {
            AttributeName: "Id", //ATTRIBUTE_NAME_1
            KeyType: "HASH"
        }
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1,
    },
    TableName: "butthisone", //TABLE_NAME
    StreamSpecification: {
        StreamEnabled: false,
    },
};

// Create DynamoDB service object
const dbclient = new DynamoDB(REGION);

const run = async () => {
    try {
        const data = await dbclient.send(new CreateTableCommand(tableParams));
        console.log("Table created.", data.TableDescription.TableName );
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.crossservice.createTableV3]
