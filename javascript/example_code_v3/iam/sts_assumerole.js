/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sts-examples-policies.html

Purpose:
sts_assumerole.test.js demonstrates how to use STS to assume an IAM Role.

Inputs (in command line below):
- REGION
- ROLE_TO_ASSUME_ARN

Running the code:
node sts_assumerule.js REGION ARN_OF_ROLE_TO_ASSUME
 */
// snippet-start:[iam.JavaScript.sts.AssumeRole]
async function run() {
    // Load the AWS SDK for Node.js
    const {STSClient, AssumeRoleCommand, GetCallerIdentityCommand} = require("@aws-sdk/client-sts");
    // Create IAM service object
    const region = process.argv[2];
    const sts = new STSClient(region);
    const roleToAssume = {RoleArn: process.argv[3] ,
        RoleSessionName: 'session1',
        DurationSeconds: 900,};
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
// snippet-end:[iam.JavaScript.sts.AssumeRole]
exports.run = run;
