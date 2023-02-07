/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_startstopinstances.js demonstrates how to start and stop an Amazon EC2 instance that is backed by Amazon Elastic Block Store.

Inputs (replace in code):
- INSTANCE_ID
- STATE: i.e., "START" or "STOP"

Running the code:
node ec2_startstopinstances.js
*/

// snippet-start:[ec2.JavaScript.Instances.start_stopInstancesV3]

// Import required AWS SDK clients and commands for Node.js.
import {
  StartInstancesCommand,
  StopInstancesCommand,
} from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";

// Set the parameters
const params = { InstanceIds: ["INSTANCE_ID"] }; // Array of INSTANCE_IDs
const command = "STATE"; // STATE i.e. "START" or "STOP"

const run = async () => {
  if (command.toUpperCase() === "START") {
    try {
      const data = await ec2Client.send(new StartInstancesCommand(params));
      console.log("Success", data.StartingInstances);
      return data;
    } catch (err) {
      console.log("Error2", err);
    }
  } else if (process.argv[2].toUpperCase() === "STOP") {
    try {
      const data = await ec2Client.send(new StopInstancesCommand(params));
      console.log("Success", data.StoppingInstances);
      return data;
    } catch (err) {
      console.log("Error", err);
    }
  }
};
run();

// snippet-end:[ec2.JavaScript.Instances.start_stopInstancesV3]
// For unit tests only.
// module.exports ={run, params};
