/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-creating-an-instance.html.

Purpose:
ec2_createinstances.js demonstrates how to create an Amazon EC2 instance.

Inputs (into command line below):
- REGION (into command line below)
- AMI_ID (into command line below)
- KEY_PAIR_NAME (into command line below)

Running the code:
    node ec2_createinstances.js REGION AMI_ID KEY_PAIR_NAME
*/
// snippet-start:[ec2.JavaScript.v3.Instances.create_instances]

async function run() {
    const {EC2, CreateTagsCommand, RunInstancesCommand} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = new EC2(region);
    var instanceParams = {
        ImageId: process.argv[3],
        InstanceType: 't2.micro',
        KeyName: process.argv[4],
        MinCount: 1,
        MaxCount: 1,

};
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
// snippet-end:[ec2.JavaScript.v3.Instances.create_instances]
exports.run = run;

