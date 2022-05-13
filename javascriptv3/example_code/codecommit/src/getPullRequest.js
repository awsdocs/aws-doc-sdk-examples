/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
getPullRequest.js gets information about a pull request in a specified repository.

Inputs (replace in code):
- PULL_REQUEST_ID

Running the code:
node getPullRequest.js
*/
// snippet-start:[codeCommit.JavaScript.getPRv3]
// Get service clients module and commands using ES6 syntax.
import { GetPullRequestCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
    pullRequestId: "PULL_REQUEST_ID"
};

// Get information about the pull request.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new GetPullRequestCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.getPRv3]

