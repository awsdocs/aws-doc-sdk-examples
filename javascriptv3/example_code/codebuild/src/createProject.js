/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is codeBuild.JavaScript.createProjectV3available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeBuild works, see https://docs.aws.amazon.com/codebuild/latest/userguide/concepts.html.

Purpose:
createProject.js demonstrates how to create an AWS CodeBuild project.

Inputs (replace in code):
- TYPE_OF_BUILD_OUTPUT
- COMPUTE_TYPE
- IMAGE_NAME
- ENVIRONMENT_TYPE
- PROJECT_NAME
- IAM_ROLE
- SOURCE_TYPE
- LOCATION_TO_BUILD_SOURCE_CODE

Note: This sample specifies the mimimum parameters required, and depending on your choices, alternative parameters may be required.
For further details on all available parameters, see https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/CodeBuild.html#createProject-property. (This is for V2, but V3 has similar
parameters).

Running the code:
node createProject.js
*/
// snippet-start:[codeBuild.JavaScript.createProjectV3]
// Get service clients module and commands using ES6 syntax.
import { CreateProjectCommand } from "@aws-sdk/client-codebuild";
import { codeBuildClient } from "./libs/codeBuildClient.js";

// Set the bucket parameters.

export const params = {
    artifacts: { /* required */
        /* Required. Options include CODEPIPELINE | S3 | NO_ARTIFACTS. */
        type: "CODEPIPELINE"
    },
    environment: { /* required */
        /* Required. Options include BUILD_GENERAL1_SMALL | BUILD_GENERAL1_MEDIUM | BUILD_GENERAL1_LARGE | BUILD_GENERAL1_2XLARGE.  */
        computeType: "BUILD_GENERAL1_SMALL",
        image: 'myiage', /* Required. */
        /* Required. Options include WINDOWS_CONTAINER | LINUX_CONTAINER | LINUX_GPU_CONTAINER | ARM_CONTAINER | WINDOWS_SERVER_2019_CONTAINER. */
        type: "BUILD_GENERAL1_SMALL"
    },
    name: 'mytestproject', /* Required. */
    /* Required. This AWS Identity and Access Management (IAM) role must have permissions to create an AWS CodeBuild project.  */
    serviceRole: 'IAM_ROLE',
    source: { /* required */
        type: "GITHUB", /* Required. Options include CODECOMMIT | CODEPIPELINE | GITHUB | S3 | BITBUCKET | GITHUB_ENTERPRISE | NO_SOURCE */
        auth: {
            type: "OAUTH" /* Required. 'OAUTH' is the only valid value. */
        },
            location: "LOCATION_TO_BUILD_SOURCE_CODE" /* Required. */
        }
};

// Create the AWS CodeBuild project.
export const run = async () => {
    try {
        const data = await codeBuildClient.send(new CreateProjectCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeBuild.JavaScript.createProjectV3]
// For unit tests only.
// module.exports ={run, bucketParams};
