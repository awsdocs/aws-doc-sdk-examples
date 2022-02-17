/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
listRepositories.js gets information about one or more repositories.

Running the code:
node listRepositories.js
*/
// snippet-start:[codeCommit.JavaScript.listReposV3]
// Get service clients module and commands using ES6 syntax.
import { ListRepositoriesCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// List your repositories.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new ListRepositoriesCommand({}));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.listReposV3]

