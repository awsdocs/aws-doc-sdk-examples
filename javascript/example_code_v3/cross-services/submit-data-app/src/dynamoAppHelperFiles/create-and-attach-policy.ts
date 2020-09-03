/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
create-cognito-id-pool.ts is part of a tutorial demonstrating how to build and deploy an app to submit
data to an Amazon DynamoDB table. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html.
create_and_attach_policy.ts demonstrates how to create an IAM policy that provides permission to
publish Amazon SNS messages, add items the table, and read access to Amazon S3. It then attaches this policy to
an IAM role.

Inputs (replace in code):
- BUCKET_NAME - The name of the Amazon S3 bucket
- POLICY_NAME -
- ROLE_NAME -

Running the code:
node create_and_attach_policy.js
 */
// snippet-start:[s3.JavaScript.crossservice.createAndAttachPolicyV3]
// Import required AWS SDK clients and commands for Node.js
const { IAM } = require("@aws-sdk/client-iam");
// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"
const bucketName = "BUCKET_NAME";
var myManagedPolicy = {
  Version: "2012-10-17",
  Statement: [
    {
      Sid: "VisualEditor0",
      Effect: "Allow",
      Action: ["sns:Publish", "dynamodb:PutItem"],
      Resource: "*",
    },
    {
      Sid: "VisualEditor1",
      Effect: "Allow",
      Action: "s3:GetObject",
      Resource: "arn:aws:s3:::" + bucketName + "/*",
    },
  ],
};

var params = {
  PolicyDocument: JSON.stringify(myManagedPolicy),
  PolicyName: "atestpolicybrmur9",
};

// Create the IAM service object
var iam = new IAM({});

const run = async () => {
  try {
    // Create the IAM policy
    const data = await iam.createPolicy(params);
    console.log("Policy created", data.Policy.Arn);
    const policy = data.Policy.Arn;
    try {
      // Set the parameters for attaching the IAM policy to an IAM role
      const attachParams = {
        PolicyArn: policy,
        RoleName: "Cognito_testerbrmur7_UnauthRole",
      };
      // Attach the IAM policy to a role
      const data = await iam.attachRolePolicy(attachParams);
      console.log("Policy attached successfully");
    } catch (err) {
      console.log("Unable to attach policy to role", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[s3.JavaScript.crossservice.createAndAttachPolicyV3]
