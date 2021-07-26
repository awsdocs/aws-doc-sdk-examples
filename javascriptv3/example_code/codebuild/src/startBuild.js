/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeBuild works,
see https://docs.aws.amazon.com/codebuild/latest/userguide/concepts.html.

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
    name: 'mynewname', /* Required. */
    serviceRole: "mynewservicerole",
    ProjectSource:
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
// snippet-start:[codeBuild.JavaScript.createProjectV3]

