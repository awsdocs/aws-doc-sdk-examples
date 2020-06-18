/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-elastic-ip-addresses.html.

Purpose:
ec2_allocateaddress.js demonstrates how to allocate and associate an Elastic IP address to an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- INSTANCE_ID (into command line below)

Running the code:
node ec2_allocateaddress.js REGION INSTANCE_ID
*/
 // snippet-start:[ec2.JavaScript.v3.Addresses.allocateAddress]
async function run(){
    const {
        EC2, AllocateAddressCommand, AssociateAddressCommand
    } = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = new EC2(region);
    const paramsAllocateAddress = {Domain: 'vpc'};
    try {
        const data = await ec2client.allocateAddress(paramsAllocateAddress);
        console.log("Address allocated:", data.AllocationId);
        var paramsAssociateAddress = {
            AllocationId: data.AllocationId,
            InstanceId: process.argv[3]
        }
    }
    catch(err){
        console.log("Address Not Allocated", err);
    }
    try{
        const results = await ec2client.send(new AssociateAddressCommand(paramsAssociateAddress))
        console.log("Address associated:", results.AssociationId);
    }
    catch(err){
        console.log("Address Not Associated", err);
    }
};
run();
// snippet-end:[ec2.JavaScript.v3.Addresses.allocateAddress]
exports.run = run;
