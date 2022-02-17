/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-elastic-ip-addresses.html

Purpose:
ec2_allocateaddress.js demonstrates how to allocate and associate an Elastic IP address to an Amazon EC2 instance.

Inputs (replace in code):
- INSTANCE_ID

Running the code:
node ec2_allocateaddress.js
*/
// snippet-start:[ec2.JavaScript.Addresses.allocateAddressV3]
// Import required AWS SDK clients and commands for Node.js
import {
  AllocateAddressCommand,
  AssociateAddressCommand,
} from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";

// Set the parameters
const paramsAllocateAddress = { Domain: "vpc" };

const run = async () => {
  try {
    const data = await ec2Client.send(
      new AllocateAddressCommand(paramsAllocateAddress)
    );
    console.log("Address allocated:", data.AllocationId);
    return data;
    var paramsAssociateAddress = {
      AllocationId: data.AllocationId,
      InstanceId: "INSTANCE_ID", //INSTANCE_ID
    };
  } catch (err) {
    console.log("Address Not Allocated", err);
  }
  try {
    const results = await ec2Client.send(
      new AssociateAddressCommand(paramsAssociateAddress)
    );
    console.log("Address associated:", results.AssociationId);
    return results;
  } catch (err) {
    console.log("Address Not Associated", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Addresses.allocateAddressV3]
// For unit tests only.
// module.exports ={run, paramsAllocateAddress};
