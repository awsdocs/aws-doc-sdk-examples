/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

//snippet-sourcedescription:[emc_template_createjob.js demonstrates how to create a transcoding job using a template.]
//snippet-service:[mediaconvert]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/emc-examples-templates.html

// snippet-start:[mediaconvert.JavaScript.templates.createJob]
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-west-2'});
// Set the custom endpoint for your acccount
AWS.config.mediaconvert = {endpoint : 'ACCOUNT_ENDPOINT'};

var params = {
  "Queue": "QUEUE_ARN",
  "JobTemplate": "TEMPLATE_NAME",
  "Role": "ROLE_ARN",
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
        "FileInput": "s3://BUCKET_NAME/FILE_NAME"
      }
    ]
  }
};

// Create a promise on a MediaConvert object
var templateJobPromise = new AWS.MediaConvert({apiVersion: '2017-08-29'}).createJob(params).promise();

// Handle promise's fulfilled/rejected status
templateJobPromise.then(
  function(data) {
    console.log("Success! ", data);
  },
  function(err) {
    console.log("Error", err);
  }
);
// snippet-end:[mediaconvert.JavaScript.templates.createJob]
