/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
describe-stack-resources.ts demonstrates how display details for AWS CloudFormation resources generated when you create an
Amazon CloudFormation stack using the Amazon Command Line Interface (CLI).
It is part of a tutorial demonstrating how create and deploy an AWS Lambda function. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/lambda-create-table-example.html.

*/
// snippet-start:[lambda.JavaScript.general-examples-lambda-create-function.describeResourcesV3]

// Load the AWS SDK for Node.js
const {
  CloudFormationClient,
  DescribeStackResourcesCommand,
  DescribeStacksCommand,
} = require("@aws-sdk/client-cloudformation");

// Create S3 service object
const cloudformation = new CloudFormationClient();

var params = {
  StackName: process.argv[2],
};

const getVariables = async () => {
  try {
    const data = await cloudformation.send(
      new DescribeStacksCommand({ StackName: params.StackName })
    );
    console.log("Status: ", data.Stacks[0].StackStatus);
    if (data.Stacks[0].StackStatus == "CREATE_COMPLETE") {
      const data = await cloudformation.send(
        new DescribeStackResourcesCommand({ StackName: params.StackName })
      );
      for (var i = 0; i < data.StackResources.length; i++) {
        var obj = data.StackResources[i].ResourceType;
        if (obj == "AWS::IAM::Policy") {
          const IDENTITY_POOL_ID = data.StackResources[i].LogicalResourceId;
          console.log("IDENTITY_POOL_ID:", IDENTITY_POOL_ID);
        }
        if (obj == "AWS::S3::Bucket") {
          const BUCKET_NAME = data.StackResources[i].PhysicalResourceId;
          console.log("BUCKET_NAME:", BUCKET_NAME);
        }
        if (obj == "AWS::IAM::Role") {
          const IAM_ROLE = data.StackResources[i].StackId;
          console.log("IAM_ROLE:", IAM_ROLE);
        }
      }
    } else {
      console.log("Stack not ready yet. Try again in a few minutes.");
    }
  } catch (err) {
    console.log("Error listing resources", err);
  }
};
getVariables();
// snippet-end:[lambda.JavaScript.general-examples-lambda-create-function.describeResourcesV3]
