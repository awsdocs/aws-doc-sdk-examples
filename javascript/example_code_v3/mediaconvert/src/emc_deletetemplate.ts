/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_deletetemplate.ts demonstrates how to delete a transcoding job template.

Inputs (replace in code):
- ACCOUNT_END_POINT
- TEMPLATE_NAME

Running the code:
ts-node emc_deletetemplate.ts
*/
// snippet-start:[mediaconvert.JavaScript.templates.deleteJobTemplateV3]
// Import required AWS-SDK clients and commands for Node.js
const {
  MediaConvert,
  DeleteJobTemplateCommand,
} = require("@aws-sdk/client-mediaconvert");

// Set the parameters
const endpoint = { endpoint: "ACCOUNT_END_POINT" }; //ACCOUNT_END_POINT
const params = { Name: "TEMPLATE_NAME" }; //TEMPLATE_NAME

//Set the MediaConvert Service Object
const mediaconvert = new MediaConvert(endpoint);

const run = async () => {
  try {
    const data = await mediaconvert.send(new DeleteJobTemplateCommand(params));
    console.log(
      "Success, template deleted! Request ID:",
      data.$metadata.requestId
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.templates.deleteJobTemplateV3]
module.exports = {run};  //for unit tests only

