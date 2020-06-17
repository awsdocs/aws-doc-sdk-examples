/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-creating-an-instance.html

Purpose:
ec2_createinstances.js demonstrates how to create an Amazon EC2 instance.

Inputs (in command line below):
- REGION (in command line below)
- AMI_ID (in command line below)
- KEY_PAIR_NAME (in command line below)

Running the code:
    node ec2_createinstances.js REGION AMI_ID KEY_PAIR_NAME
*/
// snippet-start:[ec2.JavaScript.Instances.create_instances]

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
// snippet-end:[ec2.JavaScript.Instances.create_instances]
exports.run = run;

