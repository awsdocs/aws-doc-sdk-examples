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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the Elastic Transcoder Developer Guide topic at
 *  https://docs.aws.amazon.com/elastictranscoder/latest/developerguide/introduction.html
 *
 */
// snippet-start:[elastictranscoder.php.create_hls_job.complete] 
// snippet-start:[elastictranscoder.php.create_hls_job.import] 
require 'vendor/autoload.php';

use Aws\ElasticTranscoder\ElasticTranscoderClient;
use Aws\Exception\AwsException;
// snippet-end:[elastictranscoder.php.create_hls_job.import] 

/**
 * Create an Elastic Transcoder job.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[elastictranscoder.php.create_hls_job.main] 
$tmp_path = '/tmp';

// Region where you setup your AWS resources.
$region = 'us-east-2';

// Create the client for Elastic Transcoder.
$transcoder_client = new ElasticTranscoderClient([
    'profile' => 'default',
    'version' => '2012-09-25',
    'region' => $region,
    'default_caching_config' => $tmp_path,
]);


$pipeline_id = '1234567890112-abcdefg';

$S3_file = 'folder/filename.txt';

// HLS Presets that will be used to create an adaptive bitrate playlist.
$hls_64k_audio_preset_id = '1351620000001-200071';
$hls_0400k_preset_id     = '1351620000001-200050';
$hls_0600k_preset_id     = '1351620000001-200040';
$hls_1000k_preset_id     = '1351620000001-200030';
$hls_1500k_preset_id     = '1351620000001-200020';
$hls_2000k_preset_id     = '1351620000001-200010';

$hls_presets = [
  'hlsAudio' => $hls_64k_audio_preset_id,
  'hls0400k' => $hls_0400k_preset_id,
  'hls0600k' => $hls_0600k_preset_id,
  'hls1000k' => $hls_1000k_preset_id,
  'hls1500k' => $hls_1500k_preset_id,
  'hls2000k' => $hls_2000k_preset_id,
];

// HLS Segment duration that will be targeted.
$segment_duration = '2';

$outputs = [];
foreach ($hls_presets as $prefix => $preset_id) {
    array_push( $outputs, [
        'Key' => $prefix . '_' . $S3_file, 
        'PresetId' => $preset_id, 
        'SegmentDuration' => $segment_duration,
    ]);
  };

// All outputs will have this prefix prepended to their output key.
$output_key_prefix = 'elastic-transcoder-samples/output/';

$playlist = [
    'Name' => 'hls_' . $S3_file,
    'Format' => 'HLSv3',
    'OutputKeys' => array_map(function($x) { return $x['Key']; }, $outputs)
  ];

// Create the job.
try {
    $create_job_result = $transcoder_client->createJob([
        'PipelineId' => $pipeline_id,
        'Input' => ['Key' => $S3_file],
        'Outputs' => $outputs,
        'OutputKeyPrefix' => $output_key_prefix,
        'Playlists' => [ $playlist ],
    ]);
    var_dump($create_job_result["Job"]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}


// snippet-end:[elastictranscoder.php.create_hls_job.main]
// snippet-end:[elastictranscoder.php.create_hls_job.complete] 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHLSJob.php demonstrates how to create an HLS Elastic Transcoder job.]
// snippet-keyword:[PHP]
// snippet-keyword:[createJob]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Elastic Transcoder]
// snippet-service:[elastictranscoder]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

?>
