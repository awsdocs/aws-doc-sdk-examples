/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/emc-examples-jobs.html

Purpose:
emc_createjob.js demonstrates how to create a transcoding job.

Inputs:
- ACCOUNT_ENDPOINT (into the command line below) '
- JOB_QUEUE_ARN (into the command line below)
- IAM_ROLE_ARN (into the command line below)
- OUTPUT_BUCKET_NAME (into the command line below; e.g. "s3://OUTPUT_BUCKET_NAME/")
- INPUT_BUCKET_AND_FILENAME (into the command line below; e.g. "s3://INPUT_BUCKET/FILE_NAME")

Running the code:
node emc_createjob.js ACCOUNT_ENDPOINT JOB_QUEUE_ARN IAM_ROLE_ARN OUTPUT_BUCKET_NAME INPUT_BUCKET_AND_FILENAME
*/
    // snippet-start:[mediaconvert.JavaScript.v3.jobs.createJob]
    // snippet-start:[mediaconvert.JavaScript.v3.jobs.createJob_config]
    // Import required AWS-SDK clients and commands for Node.js
    const {MediaConvert, CreateJobCommand} = require("@aws-sdk/client-mediaconvert");
    // Create a new service object and set MediaConvert to customer endpoint
    const endpoint = {endpoint: process.argv[2]}; //ACCOUNT_ENDPOINT
    const mediaconvert = new MediaConvert(endpoint);

    // snippet-end:[mediaconvert.JavaScript.v3.jobs.createJob_config]
    // snippet-start:[mediaconvert.JavaScript.v3.jobs.createJob_define]
    const params = {
      "Queue": process.argv[3], //JOB_QUEUE_ARN
      "UserMetadata": {
        "Customer": "Amazon"
      },
      "Role": process.argv[4], //IAM_ROLE_ARN
      "Settings": {
        "OutputGroups": [
          {
            "Name": "File Group",
            "OutputGroupSettings": {
              "Type": "FILE_GROUP_SETTINGS",
              "FileGroupSettings": {
                "Destination": process.argv[5] //OUTPUT_BUCKET_NAME, e.g., "s3://BUCKET_NAME/"
              }
            },
            "Outputs": [
              {
                "VideoDescription": {
                  "ScalingBehavior": "DEFAULT",
                  "TimecodeInsertion": "DISABLED",
                  "AntiAlias": "ENABLED",
                  "Sharpness": 50,
                  "CodecSettings": {
                    "Codec": "H_264",
                    "H264Settings": {
                      "InterlaceMode": "PROGRESSIVE",
                      "NumberReferenceFrames": 3,
                      "Syntax": "DEFAULT",
                      "Softness": 0,
                      "GopClosedCadence": 1,
                      "GopSize": 90,
                      "Slices": 1,
                      "GopBReference": "DISABLED",
                      "SlowPal": "DISABLED",
                      "SpatialAdaptiveQuantization": "ENABLED",
                      "TemporalAdaptiveQuantization": "ENABLED",
                      "FlickerAdaptiveQuantization": "DISABLED",
                      "EntropyEncoding": "CABAC",
                      "Bitrate": 5000000,
                      "FramerateControl": "SPECIFIED",
                      "RateControlMode": "CBR",
                      "CodecProfile": "MAIN",
                      "Telecine": "NONE",
                      "MinIInterval": 0,
                      "AdaptiveQuantization": "HIGH",
                      "CodecLevel": "AUTO",
                      "FieldEncoding": "PAFF",
                      "SceneChangeDetect": "ENABLED",
                      "QualityTuningLevel": "SINGLE_PASS",
                      "FramerateConversionAlgorithm": "DUPLICATE_DROP",
                      "UnregisteredSeiTimecode": "DISABLED",
                      "GopSizeUnits": "FRAMES",
                      "ParControl": "SPECIFIED",
                      "NumberBFramesBetweenReferenceFrames": 2,
                      "RepeatPps": "DISABLED",
                      "FramerateNumerator": 30,
                      "FramerateDenominator": 1,
                      "ParNumerator": 1,
                      "ParDenominator": 1
                    }
                  },
                  "AfdSignaling": "NONE",
                  "DropFrameTimecode": "ENABLED",
                  "RespondToAfd": "NONE",
                  "ColorMetadata": "INSERT"
                },
                "AudioDescriptions": [
                  {
                    "AudioTypeControl": "FOLLOW_INPUT",
                    "CodecSettings": {
                      "Codec": "AAC",
                      "AacSettings": {
                        "AudioDescriptionBroadcasterMix": "NORMAL",
                        "RateControlMode": "CBR",
                        "CodecProfile": "LC",
                        "CodingMode": "CODING_MODE_2_0",
                        "RawFormat": "NONE",
                        "SampleRate": 48000,
                        "Specification": "MPEG4",
                        "Bitrate": 64000
                      }
                    },
                    "LanguageCodeControl": "FOLLOW_INPUT",
                    "AudioSourceName": "Audio Selector 1"
                  }
                ],
                "ContainerSettings": {
                  "Container": "MP4",
                  "Mp4Settings": {
                    "CslgAtom": "INCLUDE",
                    "FreeSpaceBox": "EXCLUDE",
                    "MoovPlacement": "PROGRESSIVE_DOWNLOAD"
                  }
                },
                "NameModifier": "_1"
              }
            ]
          }
        ],
        "AdAvailOffset": 0,
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
        ],
        "TimecodeConfig": {
          "Source": "EMBEDDED"
        }
      }
    };
    // snippet-end:[mediaconvert.JavaScript.v3.jobs.createJob_define]

    // snippet-start:[mediaconvert.JavaScript.v3.jobs.createJob_create]
async function run(){
  try {
    const data = await mediaconvert.send(new CreateJobCommand(params));
    console.log("Job created!", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[mediaconvert.JavaScript.v3.jobs.createJob_create]
// snippet-end:[mediaconvert.JavaScript.v3.jobs.createJob]
exports.run = run; //for unit tests only
