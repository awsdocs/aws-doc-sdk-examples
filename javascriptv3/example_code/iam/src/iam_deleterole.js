/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
iam_deleterole.js demonstrates how to delete an IAM role.

Inputs :
- ROLE_NAME

Running the code:
node iam_deleterole.js
 */

// snippet-start:[iam.JavaScript.users.deleterolev3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { DeleteRoleCommand } from "@aws-sdk/client-iam";

// Set the parameters.
const params = {
    RoleName: "ROLE_NAME"
}

const run = async () => {
    try {
        const data = await iamClient.send(new DeleteRoleCommand(params));
        console.log("Success. Role deleted.", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[iam.JavaScript.users.deleterolev3]

