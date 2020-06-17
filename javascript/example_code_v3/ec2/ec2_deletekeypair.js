/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-key-pairs.html

Purpose:
ec2_deletekeypair.js demonstrates how to delete a key pair from an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- KEY_PAIR_NAME (into command line below)

Running the code:
node ec2_deletekeypair.js REGION KEY_PAIR_NAME
 */
// snippet-start:[ec2.JavaScript.keypairs.deleteKeyPair]
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
// snippet-end:[ec2.JavaScript.keypairs.deleteKeyPair]
exports.run = run;
