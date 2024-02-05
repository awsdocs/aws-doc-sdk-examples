<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the Elastic Transcoder Developer Guide topic at
 *  https://docs.aws.amazon.com/elastictranscoder/latest/developerguide/introduction.html
 *
 */
// snippet-start:[elastictranscoder.php.create_job.complete]
// snippet-start:[elastictranscoder.php.create_job.import]
require 'vendor/autoload.php';

use Aws\ElasticTranscoder\ElasticTranscoderClient;
use Aws\Exception\AwsException;

// snippet-end:[elastictranscoder.php.create_job.import]

/**
 * Create an Elastic Transcoder job.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[elastictranscoder.php.create_job.main]
$tmp_path = '/tmp';

// Region where you set up your AWS resources.
$region = 'us-east-2';

// Create the client for Elastic Transcoder.
$transcoder_client = new ElasticTranscoderClient([
    'profile' => 'default',
    'version' => '2012-09-25',
    'region' => $region,
    'default_caching_config' => $tmp_path,
]);

// This will generate a 480p 16:9 mp4 output.
$preset_id = '1351620000001-000020';

$pipeline_id = '1234567890112-abcdefg';
$S3_file = 'folder/filename.txt';

// All outputs will have this prefix prepended to their output key.
$output_key_prefix = 'elastic-transcoder-samples/output/';
$outputs = [
    [
        'Key' => $S3_file,
        'PresetId' => $preset_id
    ]
];

// Create the job.
try {
    $create_job_result = $transcoder_client->createJob([
        'PipelineId' => $pipeline_id,
        'Input' => ['Key' => $S3_file],
        'Outputs' => $outputs,
        'OutputKeyPrefix' => $output_key_prefix
    ]);
    var_dump($create_job_result["Job"]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[elastictranscoder.php.create_job.main]
// snippet-end:[elastictranscoder.php.create_job.complete]
