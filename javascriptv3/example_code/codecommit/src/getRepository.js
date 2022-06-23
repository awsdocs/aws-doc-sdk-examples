/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
getRepository.js returns information about a repository.

Inputs (replace in code):
- REPOSITORY_NAME

Running the code:
node getRepository.js
*/
// snippet-start:[codeCommit.JavaScript.getRepoV3]
// Get service clients module and commands using ES6 syntax.
import { GetRepositoryCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
    repositoryName: "REPOSITORY_NAME"
};

// Returns information about a repository.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new GetRepositoryCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.getRepoV3]

