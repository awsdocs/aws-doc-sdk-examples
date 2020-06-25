/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_startstopinstances.js demonstrates how to start and stop an Amazon EC2 instance that is backed by Amazon Elastic Block Store.

Inputs:
- REGION (into command line below)
- INSTANCE_ID (into command line below)
- COMMAND (into command line below; can be "START" or "STOP")

Running the code:
node ec2_startstopinstances.js REGION  INSTANCE_ID COMMAND
*/

// snippet-start:[ec2.JavaScript.v3.Instances.start_stopInstances]
// Import required AWS SDK clients and commands for Node.js
const {EC2, StartInstancesCommand, StopInstancesCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
var params = {InstanceIds: [process.argv[3]]};
const command = process.argv[4]; // i.e. "START" or "STOP"

async function run() {
    if (command.toUpperCase() === "START") {
        try {
            var data = await ec2client.send(new StartInstancesCommand(params));
            console.log("Success", data.StartingInstances);
        } catch (err) {
            console.log("Error2", err);
        }
    }
    else if (process.argv[2].toUpperCase() === "STOP") {
        try {
            const data = await ec2client.send(new StopInstancesCommand(params));
            console.log("Success", data.StoppingInstances);
        } catch (err) {
            console.log("Error", err)
        }
    }
};
run();

// snippet-end:[ec2.JavaScript.v3.Instances.start_stopInstances]
exports.run = run; //for unit tests only
