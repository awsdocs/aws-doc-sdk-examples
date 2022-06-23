/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
createBranch.js creates a branch in a repository and points the branch to a commit..

Inputs (replace in code):
- BRANCH_NAME
- COMMIT_ID
- REPOSITORY_NAME

Running the code:
node createRepository.js
*/
// snippet-start:[codeCommit.JavaScript.createRepositoryV3]
// Get service clients module and commands using ES6 syntax.
import { CreateBranchCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
    branchName: 'BRANCH_NAME', /* required */
    commitId: 'COMMIT_ID', /* required. The ID of the commit to point the new branch to. */
    repositoryName: 'REPOSITORY_NAME' /* required */
};

// Create the AWS CodeDeploy branch.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new CreateBranchCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.createRepositoryV3]

