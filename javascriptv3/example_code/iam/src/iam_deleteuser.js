/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_deleteuser.js demonstrates how to delete an IAM user from an AWS account.

Inputs :
- USER_NAME

Running the code:
node iam_deleteuser.js
 */
// snippet-start:[iam.JavaScript.users.deleteUserV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { DeleteUserCommand, GetUserCommand } from "@aws-sdk/client-iam";

// Set the parameters.
export const params = { UserName: "USER_NAME" }; //USER_NAME

export const run = async () => {
  try {
    const data = await iamClient.send(new GetUserCommand(params));
    return data;
    try {
      const results = await iamClient.send(new DeleteUserCommand(params));
      console.log("Success", results);
      return results;
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("User " + "USER_NAME" + " does not exist.");
  }
};
run();
// snippet-end:[iam.JavaScript.users.deleteUserV3]

