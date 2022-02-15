/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_getrole.js demonstrates how to retrieve information about the specified role, including the role path, GUID, ARN, and the trust policy that grants permission to assume the role.

Inputs :
- ROLE_NAME (optional)

Running the code:
node iam_getrole.js
 */

// snippet-start:[iam.JavaScript.getRoleV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { GetRoleCommand } from "@aws-sdk/client-iam";

// Set the parameters.
const params = {
  RoleName: "ROLE_NAME" /* required */
};

const run = async () => {
  try {
    const data = await iamClient.send(new GetRoleCommand(params));
    console.log("Success", data.Role);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.getRoleV3]
