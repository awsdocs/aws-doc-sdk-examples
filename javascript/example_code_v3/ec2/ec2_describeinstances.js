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
ec2_describeinstances.js demonstrates how to retrieve information about one or more Amazon EC2 instances.

Inputs:
- REGION (in command line input below)

Running the code:
node ec2_describeinstances.js REGION
 */

// snippet-start:[ec2.JavaScript.Instances.describeInstances]
async function run(){
  try {
    const region = process.argv[2];
    const {EC2, DescribeInstancesCommand} = require("@aws-sdk/client-ec2");
    const ec2client = await new EC2(region);
    const data = await ec2client.send(new DescribeInstancesCommand({}))
    console.log("Success", JSON.stringify(data));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Instances.describeInstances]
exports.run = run;
