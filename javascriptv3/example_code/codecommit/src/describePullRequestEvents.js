/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. For information about how AWS CodeCommit works,
see https://docs.aws.amazon.com/codecommit/latest/userguide/welcome.html.

Purpose:
describePullRequestEvents.js returns information about one or more pull request events.

Inputs (replace in code):
- PULL_REQUEST_ID
- EVENT_TYPE (optional)

Running the code:
node describePullRequestEvents.js
*/
// snippet-start:[codeCommit.JavaScript.describePREventsV3]
// Get service clients module and commands using ES6 syntax.
import { DescribePullRequestEventsCommand } from "@aws-sdk/client-codecommit";
import { codeCommitClient } from "./libs/codeCommitClient.js";

// Set the parameters.

export const params = {
    pullRequestId: 'PULL_REQUEST_ID', /* required */
    /* Optional. Options include PULL_REQUEST_CREATED | PULL_REQUEST_STATUS_CHANGED | PULL_REQUEST_SOURCE_REFERENCE_UPDATED |
    PULL_REQUEST_MERGE_STATE_CHANGED | PULL_REQUEST_APPROVAL_RULE_CREATED | PULL_REQUEST_APPROVAL_RULE_UPDATED |
    PULL_REQUEST_APPROVAL_RULE_DELETED | PULL_REQUEST_APPROVAL_RULE_OVERRIDDEN | PULL_REQUEST_APPROVAL_STATE_CHANGED */
    pullRequestEventType: 'EVENT_TYPE'
};

// Describe the PR event.
export const run = async () => {
    try {
        const data = await codeCommitClient.send(new DescribePullRequestEventsCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[codeCommit.JavaScript.describePREventsV3]

