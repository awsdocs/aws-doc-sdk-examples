/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-elastic-ip-addresses.html

Purpose:
ec2_allocateaddress.ts demonstrates how to allocate and associate an Elastic IP address to an AWS EC2 instance.

Inputs (replace in code):
- REGION
- INSTANCE_ID

Running the code:
ts-node ec2_allocateaddress.ts
*/
// snippet-start:[ec2.JavaScript.Addresses.allocateAddressV3]
// Import required AWS SDK clients and commands for Node.js
const {
  EC2Client,
  AllocateAddressCommand,
  AssociateAddressCommand
} = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const paramsAllocateAddress = { Domain: "vpc" };

// Create EC2 service object
const ec2client = new EC2Client(REGION);

const run = async () => {
  try {
    const data = await ec2client.send(new AllocateAddressCommand(paramsAllocateAddress));
    console.log("Address allocated:", data.AllocationId);
    var paramsAssociateAddress = {
      AllocationId: data.AllocationId,
      InstanceId: "INSTANCE_ID", //INSTANCE_ID
    };
  } catch (err) {
    console.log("Address Not Allocated", err);
  }
  try {
    const results = await ec2client.send(new AssociateAddressCommand(paramsAssociateAddress));
    console.log("Address associated:", results.AssociationId);
  } catch (err) {
    console.log("Address Not Associated", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Addresses.allocateAddressV3]

