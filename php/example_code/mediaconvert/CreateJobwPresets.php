<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-jobs.html
 *
 */
// snippet-start:[mediaconvert.php.create_job_with_preset.complete]
// snippet-start:[mediaconvert.php.create_job_with_preset.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\MediaConvert\MediaConvertClient;

// snippet-end:[mediaconvert.php.create_job_with_preset.import]

/**
 * Create a Job for AWS Elemental MediaConvert using Presets.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an AWSMediaConvert client object with your account specific endpoint.
// snippet-start:[mediaconvert.php.create_job_with_preset.region]
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default'
]);
// snippet-end:[mediaconvert.php.create_job_with_preset.region]
// snippet-start:[mediaconvert.php.create_job_with_preset.jobsettings]
$jobSetting = [
    "OutputGroups" => [
        [
            "Name" => "File Group",
            "OutputGroupSettings" => [
                "Type" => "FILE_GROUP_SETTINGS",
                "FileGroupSettings" => [
                    "Destination" => "s3://OUTPUT_BUCKET_NAME/"
                ]
            ],
            "Outputs" => [
                [
                    // Will use default Audio Source 1
                    "Preset" => "System-Generic_Hd_Mp4_Avc_Aac_16x9_1280x720p_24Hz_4.5Mbps",
                    "NameModifier" => "_1"
                ],
                [
                    // Will use Audio Source 2 via overloading the parameter
                    "Preset" => "System-Generic_Hd_Mp4_Avc_Aac_16x9_1280x720p_24Hz_4.5Mbps",
                    "NameModifier" => "_2",
                    "AudioDescriptions" => [
                        [
                            "AudioSourceName" => "Audio Selector 2",
                            "Codec" => "AAC",
                            "CodecSettings" => [
                                "AacSettings" => [
                                    "CodingMode" => 'CODING_MODE_2_0',
                                    "SampleRate" => 48000
                                ],
                            ]
                        ]
                    ]
                ]
            ]
        ]
    ],
    "AdAvailOffset" => 0,
    "Inputs" => [
        [
            "AudioSelectors" => [
                "Audio Selector 1" => [
                    "Offset" => 0,
                    "DefaultSelection" => "NOT_DEFAULT",
                    "ProgramSelection" => 1,
                    "SelectorType" => "TRACK",
                    "Tracks" => [
                        1
                    ]
                ],
                "Audio Selector 2" => [
                    "Offset" => 0,
                    "DefaultSelection" => "NOT_DEFAULT",
                    "ProgramSelection" => 1,
                    "SelectorType" => "TRACK",
                    "Tracks" => [
                        2
                    ]
                ]
            ],
            "VideoSelector" => [
                "ColorSpace" => "FOLLOW"
            ],
            "FilterEnable" => "AUTO",
            "PsiControl" => "USE_PSI",
            "FilterStrength" => 0,
            "DeblockFilter" => "DISABLED",
            "DenoiseFilter" => "DISABLED",
            "TimecodeSource" => "EMBEDDED",
            "FileInput" => "s3://INPUT_BUCKET_AND_FILE_NAME"
        ]
    ],
    "TimecodeConfig" => [
        "Source" => "EMBEDDED"
    ]
];
// snippet-end:[mediaconvert.php.create_job_with_preset.jobsettings]
// snippet-start:[mediaconvert.php.create_job_with_preset.main]
try {
    $result = $mediaConvertClient->createJob([
        "Role" => "IAM_ROLE_ARN",
        "Settings" => $jobSetting, //JobSettings structure
        "Queue" => "JOB_QUEUE_ARN",
        "UserMetadata" => [
            "Customer" => "Amazon"
        ],
    ]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[mediaconvert.php.create_job_with_preset.main]
// snippet-end:[mediaconvert.php.create_job_with_preset.complete]
