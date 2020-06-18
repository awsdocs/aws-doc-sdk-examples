/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-key-pairs.html.

Purpose:
ec2_deletekeypair.js demonstrates how to delete a key pair from an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- KEY_PAIR_NAME (into command line below)

Running the code:
node ec2_deletekeypair.js REGION KEY_PAIR_NAME
 */
// snippet-start:[ec2.JavaScript.v3.keypairs.deleteKeyPair]
async function run(){
   try {
      const {EC2, DeleteKeyPairCommand} = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      const params = {KeyName: process.argv[3]};
      const data = await ec2client.send(new DeleteKeyPairCommand(params))
       console.log("Key Pair Deleted");
      }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.v3.keypairs.deleteKeyPair]
exports.run = run;
