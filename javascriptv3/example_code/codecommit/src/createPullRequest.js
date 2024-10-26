// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
createProject.js Creates a pull request in the specified repository using AWS CodeCommit.

Inputs (replace in code):
- REPOSITORY_NAME
- SOURCE_REFERENCE
- DESTINATION_REFERENCE


Running the code:
node createPullRequest.js
*/
// snippet-start:[codeCommit.JavaScript.createPRV3]
// Get service clients module and commands using ES6 syntax.
import { CreatePullRequestCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
  repositoryName: "STRING_VALUE" /* required */,
  sourceReference:
    "STRING_VALUE" /* required. The branch of the repository that contains the changes for the pull request. */,
  destinationReference:
    "STRING_VALUE" /* The branch of the repository where the pull request changes are merged. */,
};

// Create the AWS CodeDeploy repository.
export const main = async () => {
  try {
    const data = await codeCommitClient.send(
      new CreatePullRequestCommand(params),
    );
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
// Call a function if this file was run directly. This allows the file
// to be runnable without running on import.
import { fileURLToPath } from "node:url";
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
// snippet-end:[codeCommit.JavaScript.createPRV3]
