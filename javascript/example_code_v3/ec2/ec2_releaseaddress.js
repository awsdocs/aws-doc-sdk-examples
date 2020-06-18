/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-elastic-ip-addresses.html.

Purpose:
ec2_releaseaddress.js demonstrates how to release an Elastic IP address.

Inputs:
- REGION (into command line  below)
- ALLOCATION_ID (into command line  below)

Running the code:
node ec2_releaseaddress.js REGION ALLOCATION_ID
*/

// snippet-start:[ec2.JavaScript.v3.Addresses.releaseAddress]
async function run(){
   try {
      const {EC2, ReleaseAddressCommand} = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      const paramsReleaseAddress = {
         AllocationId: process.argv[3]
      };
      const data = await ec2client.send(new ReleaseAddressCommand({}));
      console.log("Address released");
   }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.v3.Addresses.releaseAddress]
exports.run = run;
