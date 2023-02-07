/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-creating-an-instance.html

Purpose:
ec2_createinstances.js demonstrates how to create an Amazon EC2 instance.

Inputs (replace in code):
- AMI_ID
- KEY_PAIR_NAME

Running the code:
node ec2_createinstances.js
*/
// snippet-start:[ec2.JavaScript.Instances.create_instancesV3]
// Import required AWS SDK clients and commands for Node.js
const {
  CreateTagsCommand,
  RunInstancesCommand,
} = require("@aws-sdk/client-ec2");
import { ec2Client } from "./libs/ec2Client";

// Set the parameters
const instanceParams = {
  ImageId: "AMI_ID", //AMI_ID
  InstanceType: "t2.micro",
  KeyName: "KEY_PAIR_NAME", //KEY_PAIR_NAME
  MinCount: 1,
  MaxCount: 1,
};

const run = async () => {
  try {
    const data = await ec2Client.send(new RunInstancesCommand(instanceParams));
    console.log(data.Instances[0].InstanceId);
    const instanceId = data.Instances[0].InstanceId;
    console.log("Created instance", instanceId);
    // Add tags to the instance
    const tagParams = {
      Resources: [instanceId],
      Tags: [
        {
          Key: "Name",
          Value: "SDK Sample",
        },
      ],
    };
    try {
      await ec2Client.send(new CreateTagsCommand(tagParams));
      console.log("Instance tagged");
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Instances.create_instancesV3]
// For unit tests only.
// module.exports ={run, instanceParams};
