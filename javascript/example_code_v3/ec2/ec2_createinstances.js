/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-creating-an-instance.html

Purpose:
ec2_createinstances.js demonstrates how to create an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- AMI_ID (into command line below)
- KEY_PAIR_NAME (into command line below)

Running the code:
    node ec2_createinstances.js REGION AMI_ID KEY_PAIR_NAME
*/
// snippet-start:[ec2.JavaScript.Instances.create_instancesV3]
// Import required AWS SDK clients and commands for Node.js
const {EC2, CreateTagsCommand,
    RunInstancesCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
const instanceParams = {
    ImageId: process.argv[3],
    InstanceType: 't2.micro',
    KeyName: process.argv[4],
    MinCount: 1,
    MaxCount: 1,
};
async function run() {

    try {
        const data = await ec2client.send(new RunInstancesCommand(instanceParams))
        console.log(data.Instances[0].InstanceId);
        var instanceId = data.Instances[0].InstanceId;
        console.log("Created instance", instanceId);
        // Add tags to the instance
        tagParams = {
            Resources: [instanceId], Tags: [
                {
                    Key: 'Name',
                    Value: 'SDK Sample'
                }
            ]
        }
        try {
            const data = await ec2client.send(new CreateTagsCommand(tagParams))
            console.log("Instance tagged");
        }
        catch (err) {
            console.log("Error", err);
        }
    }
    catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[ec2.JavaScript.Instances.create_instancesV3]
exports.run = run; //for unit tests only

