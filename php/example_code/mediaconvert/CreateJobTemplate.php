<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[mediaconvert.php.create_job_template.complete]
// snippet-start:[mediaconvert.php.create_job_template.import]
require 'vendor/autoload.php';

use Aws\MediaConvert\MediaConvertClient;
use Aws\Exception\AwsException;
// snippet-end:[mediaconvert.php.create_job_template.import]

/*
 * Creates a job template for AWS Elemental MediaConvert.
 */

// snippet-start:[mediaconvert.php.create_job_template.main]
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-1',
    'profile' => 'default', 
    'endpoint' => 'https://lxEXAMPLE.mediaconvert.us-east-1.amazonaws.com'
]);

$settingsJSON = '{
    "AdAvailOffset": 0,
    "OutputGroups": [{
        "Name": "File Group",
        "OutputGroupSettings": {
            "Type": "FILE_GROUP_SETTINGS",
            "FileGroupSettings": {}
        },
        "Outputs": [{
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
                        "FramerateControl": "INITIALIZE_FROM_SOURCE",
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
                        "ParControl": "INITIALIZE_FROM_SOURCE",
                        "NumberBFramesBetweenReferenceFrames": 2,
                        "RepeatPps": "DISABLED",
                        "DynamicSubGop": "STATIC",
                        "Bitrate": 5000000
                    }
                },
                "AfdSignaling": "NONE",
                "DropFrameTimecode": "ENABLED",
                "RespondToAfd": "NONE",
                "ColorMetadata": "INSERT"
            },
            "AudioDescriptions": [{
                "AudioTypeControl": "FOLLOW_INPUT",
                "CodecSettings": {
                    "Codec": "AAC",
                    "AacSettings": {
                        "AudioDescriptionBroadcasterMix": "NORMAL",
                        "Bitrate": 96000,
                        "RateControlMode": "CBR",
                        "CodecProfile": "LC",
                        "CodingMode": "CODING_MODE_2_0",
                        "RawFormat": "NONE",
                        "SampleRate": 48000,
                        "Specification": "MPEG4"
                    }
                },
                "LanguageCodeControl": "FOLLOW_INPUT"
            }],
            "ContainerSettings": {
                "Container": "MP4",
                "Mp4Settings": {
                    "CslgAtom": "INCLUDE",
                    "CttsVersion": 0,
                    "FreeSpaceBox": "EXCLUDE",
                    "MoovPlacement": "PROGRESSIVE_DOWNLOAD"
                }
            }
        }]
    }],
    "Inputs": [{
        "FilterEnable": "AUTO",
        "PsiControl": "USE_PSI",
        "FilterStrength": 0,
        "DeblockFilter": "DISABLED",
        "DenoiseFilter": "DISABLED",
        "TimecodeSource": "EMBEDDED",
        "VideoSelector": {
            "ColorSpace": "FOLLOW",
            "Rotate": "DEGREE_0",
            "AlphaBehavior": "DISCARD"
        },
        "AudioSelectors": {
            "Audio Selector 1": {
                "Offset": 0,
                "DefaultSelection": "DEFAULT",
                "ProgramSelection": 1
            }
        }
    }]
}';

$settings = json_decode($settingsJSON, true);

try {
    $result = $mediaConvertClient->createJobTemplate([
        "Name" => "my-job-template",
        "Settings" => $settings
    ]);
} catch (AwsException $e) {
    echo 'Error: ' . $e['message'];
}
// snippet-end:[mediaconvert.php.create_job_template.main]
// snippet-end:[mediaconvert.php.create_job_template.complete] 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateJobTemplate.php demonstrates how to create template that can be used to create a AWS Elemental MediaConvert Job.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[AWS Elemental MediaConvert]
// snippet-service:[mediaconvert]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-06-01]
// snippet-sourceauthor:[pccornel (AWS)]

