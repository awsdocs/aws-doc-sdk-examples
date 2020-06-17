/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-policies.html

Purpose:
iam_createpolicy.test.js demonstrates how to create a managed policy for an AWS account.

Inputs (in command line below):
- REGION
- RESOURCE_ARN
- DYNAMODB_POLICY_NAME (e.g. myDynamoDBName)

Running the code:
node iam_createpolicy.js REGION RESOURCE_ARN DYNAMODB_POLICY_NAME
 */
// snippet-start:[iam.JavaScript.policies.createPolicy]
async function run() {
    // Load the AWS SDK for Node.js
    const {IAMClient, CreatePolicyCommand} = require("@aws-sdk/client-iam");
    // Create IAM service object
    const region = process.argv[2];
    const iam = new IAMClient(region);
    try {
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
        const data = await iam.send(new CreatePolicyCommand(params));
        console.log("Success", data);
    } catch (err) {
        console.log('Error', err);
    }
};
run();
// snippet-end:[iam.JavaScript.policies.createPolicy]
exports.run = run;
