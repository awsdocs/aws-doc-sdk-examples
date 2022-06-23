/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-getendpoint.html.

Purpose:
emc_getendpoint.js demonstrates how to retrieve information about the endpoints for an AWS account.

Running the code:
node emc_getendpoint.js
*/

// snippet-start:[mediaconvert.JavaScript.endoint.describeEndpointsV3]
// Import required AWS-SDK clients and commands for Node.js
import { DescribeEndpointsCommand } from  "@aws-sdk/client-mediaconvert";
import { emcClientGet } from  "./libs/emcClientGet.js";

//set the parameters.
const params = { MaxResults: 0 };

const run = async () => {
  try {
    // Create a new service object and set MediaConvert to customer endpoint
    const data = await emcClientGet.send(new DescribeEndpointsCommand(params));
    console.log("Your MediaConvert endpoint is ", data.Endpoints);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.endoint.describeEndpointsV3]
// For unit tests only.
// module.exports ={run, params};
