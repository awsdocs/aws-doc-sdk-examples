/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sts-examples-policies.html.

Purpose:
sts_assumerole.ts demonstrates how to use AWS STS to assume an IAM role.

Inputs :
- REGION
- ROLE_TO_ASSUME_ARN

Running the code:
node sts_assumerule.ts  ARN_OF_ROLE_TO_ASSUME
 */
// snippet-start:[iam.JavaScript.sts.AssumeRoleV3]

// Import required AWS SDK clients and commands for Node.js
import {
  STSClient,
  AssumeRoleCommand,
  GetCallerIdentityCommand,
}  from "@aws-sdk/client-sts";

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create STS service object
const sts = new STSClient(REGION);

// Set the parameters
const roleToAssume = {
  RoleArn: "ARN_OF_ROLE_TO_ASSUME", //ARN_OF_ROLE_TO_ASSUME
  RoleSessionName: "session1",
  DurationSeconds: 900,
};

const run = async () => {
  try {
    //Assume Role
    const data = await sts.send(new AssumeRoleCommand(roleToAssume));
    const rolecreds = {
      accessKeyId: data.Credentials.AccessKeyId,
      secretAccessKey: data.Credentials.SecretAccessKey,
      sessionToken: data.Credentials.SessionToken,
    };
    //Get Amazon Resource Name (ARN) of current identity
    try {
      const stsParams = { credentials: rolecreds };
      const stsClient = new STSClient(stsParams);
      const results = await stsClient.send(
        new GetCallerIdentityCommand(rolecreds)
      );
      console.log("Success", results);
    } catch (err) {
      console.log(err, err.stack);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.sts.AssumeRoleV3]
exports.run = run; //for unit tests only
