/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_listattachedrolepriorities.js demonstrates how to list all managed policies that are attached to the specified IAM role.

Inputs :
- GROUP_NAME (required)

Running the code:
node iam_listattachedrolepriorities.js
 */

// snippet-start:[iam.JavaScript.listattachedrolepoliciesV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import {ListAttachedRolePoliciesCommand} from "@aws-sdk/client-iam";

// Set the parameters.
export const params = {
    RoleName: 'ROLE_NAME' /* required */
};

export const run = async () => {
    try {
        const data = await iamClient.send(new ListAttachedRolePoliciesCommand(params));
        console.log("Success", data.AttachedPolicies);
    } catch (err) {
        console.log("Error", err);
    }
}
run();
// snippet-end:[iam.JavaScript.listattachedrolepoliciesV3]