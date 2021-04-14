/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
create-cognito-id-pool.ts is part of a tutorial demonstrating how to build and deploy an app to submit
data to an Amazon DynamoDB table.
 To run the full tutorial, see https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html.
create-cognito-id-pool.ts demonstrates how to create an Amazon Cognito identity pool with an unauthenticated IAM role.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_NAME

Running the code:
node create-cognito-id-pool.js
 */
// snippet-start:[s3.JavaScript.cross-service.createRoleV3]
// Import required AWS SDK clients and commands for Node.js
const {
  CognitoIdentity,
  SetIdentityPoolRolesCommand,
  CreateIdentityPoolCommand,
} = require("@aws-sdk/client-cognito-identity");
const { IAMClient, CreateRoleCommand } = require("@aws-sdk/client-iam");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters for creating the identity pool
const createPoolParams = {
  AllowClassicFlow: true,
  AllowUnauthenticatedIdentities: true,
  IdentityPoolName: "IDENTITY_POOL_NAME", //IDENTITY_POOL_NAME
};

// Set the parameters for creating the IAM role
// Define the JSON for the trust relationship
const trustRelationship = {
  Version: "2012-10-17",
  Statement: [
    {
      Effect: "Allow",
      Principal: {
        Federated: "cognito-identity.amazonaws.com",
      },
      Action: "sts:AssumeRoleWithWebIdentity",
      Condition: {
        "ForAnyValue:StringLike": {
          "cognito-identity.amazonaws.com:amr": "unauthenticated",
        },
      },
    },
  ],
};
// Stringify the Amazon IAM role trust relationship
const trustPolicy = JSON.stringify(trustRelationship);

// Set the parameters for attaching the role to the policy
const params = {
  AssumeRolePolicyDocument: trustPolicy,
  Path: "/",
  RoleName: "Cognito_" + createPoolParams.IdentityPoolName + "_UnauthRole",
};

// Create the IAM and Cognito service objects
const iamClient = new IAMClient({});
const CogClient = new CognitoIdentity({});

const run = async () => {
  try {
    // Create the identity pool
    const data = await CogClient.send(
      new CreateIdentityPoolCommand(createPoolParams)
    );
    console.log("Identity pool created", data.IdentityPoolId);
    const newPoolID = data.IdentityPoolId;
    try {
      //create the unauthenticaed IAM role
      const data = await iamClient.send(new CreateRoleCommand(params));
      console.log("Role created", data.Role.Arn);
      const roleARN = data.Role.Arn;
      try {
        // Attach the unauthenticated role to the identity pool
        const attachRoleParams = {
          IdentityPoolId: newPoolID,
          Roles: {
            unauthenticated: roleARN,
          },
        };
        const data = await CogClient.send(
          new SetIdentityPoolRolesCommand(attachRoleParams)
        );
        console.log(
          "Role " +
            params.RoleName +
            " added to identity pool " +
            createPoolParams.IdentityPoolName
        );
      } catch (err) {
        console.log("Error", err);
      }
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.cross-service.createRoleV3]
//for unit tests only
exports.run = run;
