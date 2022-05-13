/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sts-examples-policies.html.

Purpose:
sts_assumerole.js demonstrates how to use AWS STS to assume an IAM role.

Inputs :
- ROLE_TO_ASSUME_ARN

Running the code:
node sts_assumerule.js
 */
// snippet-start:[iam.JavaScript.sts.AssumeRoleV3]
// Import required AWS SDK clients and commands for Node.js
import { stsClient } from "./libs/stsClient.js";
import {
  AssumeRoleCommand,
  GetCallerIdentityCommand,
} from "@aws-sdk/client-sts";

// Set the parameters
export const params = {
  RoleArn: "ARN_OF_ROLE_TO_ASSUME", //ARN_OF_ROLE_TO_ASSUME
  RoleSessionName: "session1",
  DurationSeconds: 900,
};

export const run = async () => {
  try {
    //Assume Role
    const data = await stsClient.send(new AssumeRoleCommand(params));
    return data;
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

