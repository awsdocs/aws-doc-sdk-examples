/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-users.html.

Purpose:
iam_listsamlproviders.js demonstrates how to list the SAML provider resource objects defined in IAM in the account.

Running the code:
node iam_listsamlproviders.js
 */

// snippet-start:[iam.JavaScript.listsamlprovidersV3]
// Import required AWS SDK clients and commands for Node.js.
import { iamClient } from "./libs/iamClient.js";
import {ListSAMLProvidersCommand} from "@aws-sdk/client-iam";

export const run = async () => {
    try {
        const results = await iamClient.send(new ListSAMLProvidersCommand({}));
        console.log("Success", results);
        return results;
    } catch (err) {
        console.log("Error", err);
    }
}
run();
// snippet-end:[iam.JavaScript.listsamlprovidersV3]