/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-managing-instances.html.

Purpose:
ec2_rebootinstances.js demonstrates how to queue a reboot request for one or more Amazon EC2 instances.

Inputs:
- REGION (into command line  below)
- INSTANCE_ID (into command line  below)

Running the code:
node ec2_rebootinstances.js
*/
// snippet-start:[ec2.JavaScript.v3.Instances.rebootInstances]
async function run() {
    const {EC2, RebootInstancesCommandInput} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = await new EC2(region);
    const params = {InstanceIds: [process.argv[3]]};
    try {
        const data = await ec2client.send(new RebootInstancesCommandInput(params));
        console.log("Success", data.InstanceMonitorings);
    } catch (err) {
                console.log("Error", err);
}
};
// snippet-end:[ec2.JavaScript.v3.Instances.rebootInstances]
exports.run = run;
