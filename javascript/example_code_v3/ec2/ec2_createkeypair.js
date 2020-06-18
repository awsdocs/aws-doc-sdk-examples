/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-key-pairs.html.

Purpose:
ec2_createkeypair.js demonstrates how to create an RSA key pair for an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- MY_KEY_PAIR (into command line below)

Running the code:
node ec2_createkeypair.js REGION MY_KEY_PAIR
 */
// snippet-start:[ec2.JavaScript.v3.keypairs.createKeyPair]
async function run(){
   try {
      const {
         EC2, CreateKeyPairCommand
      } = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      const params = {
         KeyName: process.argv[3]
      };
      const data = await ec2client.send(new CreateKeyPairCommand(params))
      console.log(JSON.stringify(data));
   }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.v3.keypairs.createKeyPair]
exports.run = run;
