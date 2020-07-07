/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-policies.html.

Purpose:
iam_createpolicy.js demonstrates how to create a managed policy for an AWS account.

Inputs (into command line below):
- REGION
- RESOURCE_ARN
- DYNAMODB_POLICY_NAME (e.g., "myDynamoDBName")

Running the code:
node iam_createpolicy.js REGION RESOURCE_ARN DYNAMODB_POLICY_NAME
 */
// snippet-start:[iam.JavaScript.policies.createPolicyV3]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, CreatePolicyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const myManagedPolicy = {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "logs:CreateLogGroup",
            "Resource": process.argv[3]
        },
        {
            "Effect": "Allow",
            "Action": [
                "dynamodb:DeleteItem",
                "dynamodb:GetItem",
                "dynamodb:PutItem",
                "dynamodb:Scan",
                "dynamodb:UpdateItem"
            ],
            "Resource": process.argv[3]
        }
    ]
};
const params = {
    PolicyDocument: JSON.stringify(myManagedPolicy),
    PolicyName: process.argv[4],
};

async function run() {
    try {
        const data = await iam.send(new CreatePolicyCommand(params));
        console.log("Success", data);
    } catch (err) {
        console.log('Error', err);
    }
};
run();
// snippet-end:[iam.JavaScript.policies.createPolicyV3]
exports.run = run; //for unit tests only
