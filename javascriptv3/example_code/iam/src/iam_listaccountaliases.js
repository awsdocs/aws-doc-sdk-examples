/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-account-aliases.html.

Purpose:
iam_listaccountaliases.js demonstrates how to retrieve information about the aliases for an AWS account.

Running the code:
node iam_listaccountaliases.js
 */
// snippet-start:[iam.JavaScript.alias.listAccountAliasesV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import { ListAccountAliasesCommand } from "@aws-sdk/client-iam";

// Set the parameters.
export const params = { MaxItems: 5 };

export const run = async () => {
  try {
    const data = await iamClient.send(new ListAccountAliasesCommand(params));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[iam.JavaScript.alias.listAccountAliasesV3]

