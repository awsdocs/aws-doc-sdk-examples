/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-elastic-ip-addresses.html

Purpose:
ec2_describeaddresses.js demonstrates how to retrieve information about one or more Elastic IP addresses.

Inputs:
- REGION (into command line below)

Running the code:
node ec2_describeaddresses.js REGION
*/
// snippet-start:[ec2.JavaScript.v3.Addresses.describeAddresses]
// Import required AWS SDK clients and commands for Node.js
const {EC2, DescribeAddressesCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
const params = {
  Filters: [
    {Name: 'domain', Values: ['vpc']}
  ]
};

async function run(){
  try {
    const data = await ec2client.send(new DescribeAddressesCommand(params))
    console.log(JSON.stringify(data.Addresses));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.v3.Addresses.describeAddresses]
exports.run = run; //for unit tests only
