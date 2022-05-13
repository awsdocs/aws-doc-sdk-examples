/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-key-pairs.html

Purpose:
ec2_describekeypairs.js demonstrates how to retrieve information about one or more key pairs.

Running the code:
node ec2_describekeypairs.js
 */
// snippet-start:[ec2.JavaScript.keypairs.describeKeyPairV3]
// Import required AWS SDK clients and commands for Node.js
import { DescribeKeyPairsCommand } from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";
const run = async () => {
  try {
    const data = await ec2Client.send(new DescribeKeyPairsCommand({}));
    console.log("Success", JSON.stringify(data.KeyPairs));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.keypairs.describeKeyPairV3]
// For unit tests only.
// module.exports ={run};
