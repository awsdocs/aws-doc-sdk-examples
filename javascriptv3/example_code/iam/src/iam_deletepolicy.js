/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
iam_deletepolicy.js demonstrates how to delete an IAM policy.

Inputs :
- POLICY_ARN

Running the code:
node iam_deletepolicy.js
 */

// snippet-start:[iam.JavaScript.users.deletepolicyv3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { DeletePolicyCommand } from "@aws-sdk/client-iam";

// Set the parameters.
const params = { PolicyArn: "POLICY_ARN"};

const run = async () => {
    try {
        const data = await iamClient.send(new DeletePolicyCommand(params));
        console.log("Success. Policy deleted.", data);

    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[iam.JavaScript.users.deletepolicyv3]

