/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_deleteaccesskey.js demonstrates how to delete the AWS access key pair for an IAM user.

Inputs(in code):
- ACCESS_KEY_ID
- USER_NAME

Running the code:
node iam_deleteaccesskey.js
 */
// snippet-start:[iam.JavaScript.keys.deleteAccessKeyV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { DeleteAccessKeyCommand } from "@aws-sdk/client-iam";

// Set the parameters.
export const params = {
  AccessKeyId: "ACCESS_KEY_ID", // ACCESS_KEY_ID
  UserName: "USER_NAME", // USER_NAME
};

export const run = async () => {
  try {
    const data = await iamClient.send(new DeleteAccessKeyCommand(params));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.keys.deleteAccessKeyV3]

