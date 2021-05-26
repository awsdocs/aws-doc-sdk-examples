/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html.

Purpose:
emc_listjobs.js demonstrates how to retrieve a list of the most-recently created transcoding jobs.

Inputs (replace in code):
- QUEUE_ARN

Running the code:
node emc_listjobs.js
*/
// snippet-start:[mediaconvert.JavaScript.jobs.listJobsV3]

// Import required AWS-SDK clients and commands for Node.js
import { ListJobsCommand } from  "@aws-sdk/client-mediaconvert";
import { emcClient }  from   "./libs/emcClient.js";

// Set the parameters
const params = {
  MaxResults: 10,
  Order: "ASCENDING",
  Queue: "QUEUE_ARN",
  Status: "SUBMITTED" // e.g., "SUBMITTED"
};

const run = async () => {
  try {
    const data = await emcClient.send(new ListJobsCommand(params));
    console.log("Success. Jobs: ", data.Jobs);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.jobs.listJobsV3]
// For unit tests only.
 module.exports ={run, params};
