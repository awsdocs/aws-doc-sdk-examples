/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sts-examples-policies.html.

Purpose:
sts_assumerole.js demonstrates how to use STS to assume an IAM Role.

Inputs (into command line below):
- REGION
- ROLE_TO_ASSUME_ARN

Running the code:
node sts_assumerule.js REGION ARN_OF_ROLE_TO_ASSUME
 */
// snippet-start:[iam.JavaScript.v3.sts.AssumeRole]
// Import required AWS SDK clients and commands for Node.js
const {STSClient, AssumeRoleCommand, GetCallerIdentityCommand} = require("@aws-sdk/client-sts");
// Set the AWS Region
const region = process.argv[2];
// Create STS service object
const sts = new STSClient(region);
// Set the parameters
const roleToAssume = {RoleArn: process.argv[3] ,
    RoleSessionName: 'session1',
    DurationSeconds: 900,};

async function run() {
    try {
        //Assume Role
        const data = await sts.send(new AssumeRoleCommand(roleToAssume));
        const rolecreds = {
            accessKeyId: data.Credentials.AccessKeyId,
            secretAccessKey: data.Credentials.SecretAccessKey,
            sessionToken: data.Credentials.SessionToken
        };
        //Get Arn of current identity
        try {
            const stsParams = {credentials: rolecreds};
            const stsClient = new STSClient(stsParams);
            const results = await stsClient.send(new GetCallerIdentityCommand(rolecreds));
            console.log("Success", results);
        } catch (err) {
            console.log(err, err.stack);
        }
    }
     catch(err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[iam.JavaScript.v3.sts.AssumeRole]
exports.run = run; //for unit tests only
