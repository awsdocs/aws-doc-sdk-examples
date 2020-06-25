/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_monitorinstances.js demonstrates how to enable detailed monitoring for Amazon EC2 instances.

Inputs:
- REGION (into command line below)
- INSTANCE_ID (into command line below)
- STATE (into command line below; ie.'ON' or 'OFF')

Running the code:
node ec2_monitorinstances.js REGION INSTANCE_ID STATE
 */

// snippet-start:[ec2.JavaScript.v3.Instances.monitorInstances]
// Import required AWS SDK clients and commands for Node.js
const {EC2, MonitorInstancesCommand, UnmonitorInstancesCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
const params = {InstanceIds: [process.argv[3]]};
const state = process.argv[4]; // 'ON' or 'OFF'

async function run() {
    if (process.argv[4].toUpperCase() === "ON") {
        try {
            const data = await ec2client.send(new MonitorInstancesCommand(params));
            console.log("Success", data.InstanceMonitorings);
        } catch (err) {
            console.log("Error", err);
        }
    }
    else if (process.argv[4].toUpperCase() === "OFF") {
        try {
            const data = await ec2client.send(new UnmonitorInstancesCommand(params));
                    console.log("Success", data.InstanceMonitorings);
        } catch (err) {
            console.log("Error", err);
                }
        }
};
run();
// snippet-end:[ec2.JavaScript.v3.Instances.monitorInstances]
exports.run = run; //for unit tests only
