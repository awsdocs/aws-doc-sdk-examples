/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
putFile.js adds or updates a file in a branch in an AWS CodeCommit repository,
and generates a commit for the addition in the specified branch.

Inputs (replace in code):
- BRANCH
- REPOSITORY_NAME
- PATH_AND_FILENAME_WITHIN_REPO
- SOURCE_PATH_AND_FILENAME (optional)

Running the code:
node putFile.js
*/
// snippet-start:[codeCommit.JavaScript.putFileV3]
// Get service clients module and commands using ES6 syntax.
import { CreateCommitCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

const BRANCH = "BRANCH";
const REPO = "REPO_NAME";

export const getBranchParams = {
  branchName: BRANCH,
  repositoryName: REPO,
};

// Add or update the file.
export const main = async () => {
  const COMMIT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxx";
  const createCommitParams = {
    branchName: BRANCH /* required */,
    repositoryName: REPO /* required */,
    parentCommitId: COMMIT_ID /* required */,
    putFiles: [
      {
        /* Required. The full path to the file in the repository,
          including the name of the file. For example, 'js/index.js'  */
        filePath: "PATH_AND_FILENAME_WITHIN_REPO",
        /* Content to be committed to the file. Required if sourceFile is not specified.*/
        fileContent: Buffer.from("STRING"),
        /* The name and full path of the file that contains the
          changes you want to make as part of the commit. Required if fileContent
          is not specified.*/
        sourceFile: {
          /* Required. The full path to the file, including the name of the file. */
          filePath: "SOURCE_PATH_AND_FILENAME",
        },
      },
      /* more items */
    ],
  };
  try {
    const data = await codeCommitClient.send(
      new CreateCommitCommand(createCommitParams),
    );
    console.log("Success", data.commitId);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
// Call a function if this file was run directly. This allows the file
// to be runnable without running on import.
import { fileURLToPath } from "url";
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
// snippet-end:[codeCommit.JavaScript.putFileV3]
