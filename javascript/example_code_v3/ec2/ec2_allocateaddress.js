/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-elastic-ip-addresses.html

Purpose:
ec2_allocateaddress.js demonstrates how to allocate and associate an Elastic IP address to an Amazon EC2 instance.

Inputs:
- REGION (in command line below)
- INSTANCE_ID (in command line below)

Running the code:
node ec2_allocateaddress.js REGION INSTANCE_ID
*/
 // snippet-start:[ec2.JavaScript.Addresses.allocateAddress]
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
// snippet-end:[ec2.JavaScript.Addresses.allocateAddress]
exports.run = run;
