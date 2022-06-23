/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_listgroups.js demonstrates how to list the IAM groups for an AWS account.

Inputs:
- ROLE_NAME (required)
- MARKER (optional)
- MAX_ITEMS (optional)


Running the code:
node iam_listgroups.js
 */

// snippet-start:[iam.JavaScript.listgroupsV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import {ListGroupsCommand} from "@aws-sdk/client-iam";

// Set the parameters.
export const params = {
    RoleName: 'ROLE_NAME', /* This is a number value. Required */
    Marker: 'MARKER', /* This is a string value. Optional */
    MaxItems: 'MAX_ITEMS' /* This is a number value. Optional */
};

export const run = async () => {
    try {
        const data = await iamClient.send(new ListGroupsCommand({}));
        console.log("Success", data.Groups);
    } catch (err) {
        console.log("Error", err);
    }
}
run();
// snippet-end:[iam.JavaScript.listgroupsV3]