/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-key-pairs.html.

Purpose:
ec2_describekeypairs.js demonstrates how to retrieve information about one or more key pairs.

Inputs:
- REGION (into command line  below)

Running the code:
node ec2_describekeypairs.js REGION
 */
// snippet-start:[ec2.JavaScript.v3.keypairs.describeKeyPair]
async function run(){
   try {
      const {EC2, DescribeKeyPairsCommand} = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      const data = await ec2client.send(new DescribeKeyPairsCommand({}));
      console.log("Success", JSON.stringify(data.KeyPairs));
   }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.v3.keypairs.describeKeyPair]
exports.run = run;
