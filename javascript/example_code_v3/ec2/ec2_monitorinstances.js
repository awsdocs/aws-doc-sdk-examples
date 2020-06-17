/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_monitorinstances.js demonstrates how to enable detailed monitoring for Amazon EC2 instances.

Inputs:
- REGION (in command line input below)
- STATE ('ON' or 'OFF') (in command line input below)
- INSTANCE_ID (in command line input below)

Running the code:
node ec2_monitorinstances.js REGION STATE INSTANCE_ID
 */
// snippet-start:[ec2.JavaScript.Instances.monitorInstances]
async function run() {
    const {EC2, MonitorInstancesCommand, UnmonitorInstancesCommand} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = await new EC2(region);
    const instance = process.argv[3];
    const params = {InstanceIds: [process.argv[4]]};
    if (process.argv[3].toUpperCase() === "ON") {
        try {
            const data = await ec2client.send(new MonitorInstancesCommand(params));
            console.log("Success", data.InstanceMonitorings);
        } catch (err) {
            console.log("Error", err);
        }
    }
    else if (process.argv[3].toUpperCase() === "OFF") {
        try {
            const data = await ec2client.send(new UnmonitorInstancesCommand(params));
                    console.log("Success", data.InstanceMonitorings);
        } catch (err) {
            console.log("Error", err);
                }
        }
};
run();
// snippet-end:[ec2.JavaScript.Instances.monitorInstances]
exports.run = run;
