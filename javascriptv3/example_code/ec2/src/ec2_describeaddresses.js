/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide//ec2-example-elastic-ip-addresses.html

Purpose:
ec2_describeaddresses.js demonstrates how to retrieve information about one or more Elastic IP addresses.


Running the code:
node ec2_describeaddresses.js
*/
// snippet-start:[ec2.JavaScript.Addresses.describeAddressesV3]

// Import required AWS SDK clients and commands for Node.js
import { EC2Client, DescribeAddressesCommand } from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";
// Set the parameters
const params = {
  Filters: [{ Name: "domain", Values: ["vpc"] }],
};

const run = async () => {
  try {
    const data = await ec2Client.send(new DescribeAddressesCommand(params));
    console.log(JSON.stringify(data.Addresses));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Addresses.describeAddressesV3]
// For unit tests only.
// module.exports ={run, params};
