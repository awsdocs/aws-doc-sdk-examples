/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
getMergeOptions.js returns information about the available merge options between two branches or commit specifiers.

Inputs (replace in code):
- DESTINATION_COMMIT_SPECIFIER
- REPOSITORY_NAME
- SOURCE_COMMIT_SPECIFIER


Running the code:
node getMergeOptions.js
*/
// snippet-start:[codeCommit.JavaScript.getMergeOptionsV3]
// Get service clients module and commands using ES6 syntax.
import { GetMergeOptionsCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
    /* required. The branch, tag, HEAD, or other fully qualified reference used to identify a commit (for example, a branch name or a full commit ID). */
    destinationCommitSpecifier: 'DESTINATION_COMMIT_SPECIFIER',
    repositoryName: 'REPOSITORY_NAME', /* required. */
    /* required. The branch, tag, HEAD, or other fully qualified reference used to identify a commit (for example, a branch name or a full commit ID).*/
    sourceCommitSpecifier: 'SOURCE_COMMIT_SPECIFIER',
};

// Get the merge options.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new GetMergeOptionsCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.getMergeOptionsV3]

