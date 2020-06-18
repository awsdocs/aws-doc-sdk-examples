/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-elastic-ip-addresses.html.

Purpose:
ec2_describeaddresses.js demonstrates how to retrieve information about one or more Elastic IP addresses.

Inputs:
- REGION (into command line  below)

Running the code:
node ec2_describeaddresses.js REGION
*/
// snippet-start:[ec2.JavaScript.v3.Addresses.describeAddresses]
async function run(){
  try {
    const params = {
      Filters: [
        {Name: 'domain', Values: ['vpc']}
      ]
    };
    const {EC2, DescribeAddressesCommand} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = await new EC2(region);
    const data = await ec2client.send(new DescribeAddressesCommand(params))
    console.log(JSON.stringify(data.Addresses));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.v3.Addresses.describeAddresses]
exports.run = run;
