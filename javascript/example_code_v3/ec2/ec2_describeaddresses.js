/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-elastic-ip-addresses.html

Purpose:
ec2_describeaddresses.js demonstrates how to retrieve information about one or more Elastic IP addresses.

Inputs (replace in code):
- REGION

Running the code:
node ec2_describeaddresses.js
*/
// snippet-start:[ec2.JavaScript.Addresses.describeAddressesV3]

// Import required AWS SDK clients and commands for Node.js
const {EC2, DescribeAddressesCommand} = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Filters: [
    {Name: 'domain', Values: ['vpc']}
  ]
};

// Create EC2 service object
const ec2client = new EC2(REGION);

const run = async () => {
  try {
    const data = await ec2client.send(new DescribeAddressesCommand(params))
    console.log(JSON.stringify(data.Addresses));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Addresses.describeAddressesV3]
exports.run = run; //for unit tests only
