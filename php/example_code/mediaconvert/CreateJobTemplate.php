<?php
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
 *
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-jobs.html
 *
 *
 *
 */
// snippet-start:[mediaconvert.php.create_job_template.complete]
// snippet-start:[mediaconvert.php.create_job_template.import]

require 'vendor/autoload.php';

use Aws\MediaConvert\MediaConvertClient;  
use Aws\Exception\AwsException;
// snippet-end:[mediaconvert.php.create_job_template.import]

/**
 * Create a Job for AWS Elemental MediaConvert.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an AWSMediaConvert client object with your account specific endpoint.  
// snippet-start:[mediaconvert.php.create_job_template.main]
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default',
    'endpoint' => 'ACCOUNT_ENDPOINT'
]);

$jobTemplate = [

    "Settings" => [
        "OutputGroups" => [
            [
                "Name" => "File Group",
                "OutputGroupSettings" => [
                    "Type" => "FILE_GROUP_SETTINGS",
                    "FileGroupSettings" => [
                        "Destination" => "s3://BUCKET_NAME/"
                    ]
                ],
                "Outputs" => [
                    [
                        "VideoDescription" => [
                            "ScalingBehavior" => "DEFAULT",
                            "TimecodeInsertion" => "DISABLED",
                            "AntiAlias" => "ENABLED",
                            "Sharpness" => 50,
                            "CodecSettings" => [
                                "Codec" => "H_264",
                                "H264Settings" => [
                                    "InterlaceMode" => "PROGRESSIVE",
                                    "NumberReferenceFrames" => 3,
                                    "Syntax" => "DEFAULT",
                                    "Softness" => 0,
                                    "GopClosedCadence" => 1,
                                    "GopSize" => 90,
                                    "Slices" => 1,
                                    "GopBReference" => "DISABLED",
                                    "SlowPal" => "DISABLED",
                                    "SpatialAdaptiveQuantization" => "ENABLED",
                                    "TemporalAdaptiveQuantization" => "ENABLED",
                                    "FlickerAdaptiveQuantization" => "DISABLED",
                                    "EntropyEncoding" => "CABAC",
                                    "Bitrate" => 5000000,
                                    "FramerateControl" => "SPECIFIED",
                                    "RateControlMode" => "CBR",
                                    "CodecProfile" => "MAIN",
                                    "Telecine" => "NONE",
                                    "MinIInterval" => 0,
                                    "AdaptiveQuantization" => "HIGH",
                                    "CodecLevel" => "AUTO",
                                    "FieldEncoding" => "PAFF",
                                    "SceneChangeDetect" => "ENABLED",
                                    "QualityTuningLevel" => "SINGLE_PASS",
                                    "FramerateConversionAlgorithm" => "DUPLICATE_DROP",
                                    "UnregisteredSeiTimecode" => "DISABLED",
                                    "GopSizeUnits" => "FRAMES",
                                    "ParControl" => "SPECIFIED",
                                    "NumberBFramesBetweenReferenceFrames" => 2,
                                    "RepeatPps" => "DISABLED",
                                    "FramerateNumerator" => 30,
                                    "FramerateDenominator" => 1,
                                    "ParNumerator" => 1,
                                    "ParDenominator" => 1
                                ]
                            ],
                            "AfdSignaling" => "NONE",
                            "DropFrameTimecode" => "ENABLED",
                            "RespondToAfd" => "NONE",
                            "ColorMetadata" => "INSERT"
                        ],
                        "AudioDescriptions" => [
                            [
                                "AudioTypeControl" => "FOLLOW_INPUT",
                                "CodecSettings" => [
                                    "Codec" => "AAC",
                                    "AacSettings" => [
                                        "AudioDescriptionBroadcasterMix" => "NORMAL",
                                        "RateControlMode" => "CBR",
                                        "CodecProfile" => "LC",
                                        "CodingMode" => "CODING_MODE_2_0",
                                        "RawFormat" => "NONE",
                                        "SampleRate" => 48000,
                                        "Specification" => "MPEG4",
                                        "Bitrate" => 64000
                                    ]
                                ],
                                "LanguageCodeControl" => "FOLLOW_INPUT",
                                "AudioSourceName" => "Audio Selector 1"
                            ]
                        ],
                        "ContainerSettings" => [
                            "Container" => "MP4",
                            "Mp4Settings" => [
                                "CslgAtom" => "INCLUDE",
                                "FreeSpaceBox" => "EXCLUDE",
                                "MoovPlacement" => "PROGRESSIVE_DOWNLOAD"
                            ]
                        ],
                        "NameModifier" => "_1"
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
            ]
        ],
        "TimecodeConfig" => [
            "Source" => "EMBEDDED"
        ]
    ]
];

try [
    $result = $mediaConvertClient->createJob([
        Category => 'YouTube Jobs',
        Description => 'Final production transcode',
        Name => 'DemoTemplate',
        Queue => 'JOB_QUEUE_ARN',

        "JobTemplate" => , //String either a job template or the transcoding settings
       "Role" => "IAM_ROLE_ARN" , 
       "Settings" => $jobSetting , //JobSettings structure 
       "Queue" => 'JOB_QUEUE_ARN',
   ]);    
]catch (AwsException $e) [
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
]

ry [$result = $mediaConvertClient->createJob([
    "Queue" => "QUEUE_ARN",
    "JobTemplate" => "TEMPLATE_NAME",
    "Role" => "ROLE_ARN",
    "Settings" => [
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
                "FileInput" => "s3://BUCKET_NAME/FILE_NAME"
            ]
        ]
    ]
]; 
 
// snippet-end:[mediaconvert.php.create_job_template.main]
// snippet-end:[mediaconvert.php.create_job_template.complete] 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateJobTemplate.php demonstrates how to create template that can be used to create a AWS Elemental MediaConvert Job.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[AWS Elemental MediaConvert]
// snippet-service:[mediaconvert]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-09-20]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

