/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release by September 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_template_createjob.ts demonstrates how to create a transcoding job using a template.

Inputs (replace in code):
- ACCOUNT_END_POINT
- QUEUE_ARN
- TEMPLATE_NAME
- ROLE_ARN
- INPUT_BUCKET_AND_FILENAME, e.g., "s3://BUCKET_NAME/FILE_NAME"

Running the code:
ts-node emc_template_createjob.ts
*/
// snippet-start:[mediaconvert.JavaScript.templates.createJobV3]
// Import required AWS-SDK clients and commands for Node.js
const {
  MediaConvert,
  CreateJobCommand,
} = require("@aws-sdk/client-mediaconvert");

//Set the parameters
const endpoint = { endpoint: "ACCOUNT_END_POINT" }; //ACCOUNT_END_POINT

const params = {
  Queue: "QUEUE_ARN", //QUEUE_ARN
  JobTemplate: "TEMPLATE_NAME", //TEMPLATE_NAME
  Role: "ROLE_ARN", //ROLE_ARN
  Settings: {
    Inputs: [
      {
        AudioSelectors: {
          "Audio Selector 1": {
            Offset: 0,
            DefaultSelection: "NOT_DEFAULT",
            ProgramSelection: 1,
            SelectorType: "TRACK",
            Tracks: [1],
          },
        },
        VideoSelector: {
          ColorSpace: "FOLLOW",
        },
        FilterEnable: "AUTO",
        PsiControl: "USE_PSI",
        FilterStrength: 0,
        DeblockFilter: "DISABLED",
        DenoiseFilter: "DISABLED",
        TimecodeSource: "EMBEDDED",
        FileInput: "INPUT_BUCKET_AND_FILENAME", //INPUT_BUCKET_AND_FILENAME, e.g., "s3://BUCKET_NAME/FILE_NAME"
      },
    ],
  },
};

//Set the MediaConvert Service Object
const mediaconvert = new MediaConvert(endpoint);

const run = async () => {
  try {
    const data = await mediaconvert.send(new CreateJobCommand(params));
    console.log("Success! ", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.templates.createJobV3]
// module.exports = {run};  //for unit tests only
