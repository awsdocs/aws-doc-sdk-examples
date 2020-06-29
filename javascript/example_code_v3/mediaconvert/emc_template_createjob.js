/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release by September 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release September 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-templates.html.

Purpose:
emc_template_createjob.js demonstrates how to create a transcoding job using a template.

Inputs: (all into command line below)
- ACCOUNT_END_POINT
- QUEUE_ARN
- TEMPLATE_NAME
- ROLE_ARN
- INPUT_BUCKET_AND_FILENAME, e.g., "s3://BUCKET_NAME/FILE_NAME"

Running the code:
node emc_template_createjob.js ACCOUNT_END_POINT QUEUE_ARN JOB_TEMPLATE_ARN ROLE_ARN FILE_INPUT
*/
// snippet-start:[mediaconvert.JavaScript.v3.templates.createJob]
// Import required AWS-SDK clients and commands for Node.js
const {MediaConvert, CreateJobCommand} = require("@aws-sdk/client-mediaconvert");
// Create a new service object and set MediaConvert to customer endpoint
const endpoint = {endpoint : process.argv[2]}; //ACCOUNT_END_POINT
const mediaconvert = new MediaConvert(endpoint);
const params = {
  "Queue": process.argv[3], //QUEUE_ARN
  "JobTemplate": process.argv[4], //TEMPLATE_NAME
  "Role": process.argv[5], //ROLE_ARN
  "Settings": {
    "Inputs": [
      {
        "AudioSelectors": {
          "Audio Selector 1": {
            "Offset": 0,
            "DefaultSelection": "NOT_DEFAULT",
            "ProgramSelection": 1,
            "SelectorType": "TRACK",
            "Tracks": [
              1
            ]
          }
        },
        "VideoSelector": {
          "ColorSpace": "FOLLOW"
        },
        "FilterEnable": "AUTO",
        "PsiControl": "USE_PSI",
        "FilterStrength": 0,
        "DeblockFilter": "DISABLED",
        "DenoiseFilter": "DISABLED",
        "TimecodeSource": "EMBEDDED",
        "FileInput": process.argv[6] //INPUT_BUCKET_AND_FILENAME, e.g., "s3://BUCKET_NAME/FILE_NAME"
      }
    ]
  }
};

async function run(){
  try {
    const data = await mediaconvert.send(new CreateJobCommand(params));
    console.log("Success! ", data);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.templates.createJob]
exports.run = run; //for unit tests only
