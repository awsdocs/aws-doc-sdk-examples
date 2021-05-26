/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_listtemplates.js demonstrates how to retrieve transcoding job templates.

Running the code:
node emc_listtemplates.js
*/
// snippet-start:[mediaconvert.JavaScript.templates.listJobTemplatesV3]

// Import required AWS-SDK clients and commands for Node.js
import { ListJobTemplatesCommand } from  "@aws-sdk/client-mediaconvert";
import { emcClient } from "./libs/emcClient.js";

const params = {
  ListBy: "NAME",
  MaxResults: 10,
  Order: "ASCENDING",
};

const run = async () => {
  try {
    const data = await emcClient.send(new ListJobTemplatesCommand(params));
    console.log("Success ", data.JobTemplates);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.templates.listJobTemplatesV3]
// For unit tests only.
// module.exports ={run, params};
