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
 *  https://alpha-docs-aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html
 *
 */
 
// Path to your PHP autoload.  If you are using a phar installation, this is the
// path to your aws.phar file.
require_once 'path/to/autoload.php';

use Aws\ElasticTranscoder\ElasticTranscoderClient;

// Region where the sample will be run.
$region = 'us-east-1';

// HLS Presets that will be used to create an adaptive bitrate playlist.
$hls_64k_audio_preset_id = '1351620000001-200071';
$hls_0400k_preset_id     = '1351620000001-200050';
$hls_0600k_preset_id     = '1351620000001-200040';
$hls_1000k_preset_id     = '1351620000001-200030';
$hls_1500k_preset_id     = '1351620000001-200020';
$hls_2000k_preset_id     = '1351620000001-200010';

$hls_presets = array(
  'hlsAudio' => $hls_64k_audio_preset_id,
  'hls0400k' => $hls_0400k_preset_id,
  'hls0600k' => $hls_0600k_preset_id,
  'hls1000k' => $hls_1000k_preset_id,
  'hls1500k' => $hls_1500k_preset_id,
  'hls2000k' => $hls_2000k_preset_id,
);

// HLS Segment duration that will be targeted.
$segment_duration = '2';

//All outputs will have this prefix prepended to their output key.
$output_key_prefix = 'elastic-transcoder-samples/output/hls/';

// Create the client for Elastic Transcoder.
$transcoder_client = ElasticTranscoderClient::factory(array('region' => $region, 'default_caching_config' => '/tmp'));

function create_hls_job($transcoder_client, $pipeline_id, $input_key, $output_key_prefix, $hls_presets, $segment_duration) {
  // Setup the job input using the provided input key.
  $input = array('Key' => $input_key);

  //Setup the job outputs using the HLS presets.
  $output_key = hash('sha256', utf8_encode($input_key));

  // Specify the outputs based on the hls presets array spefified.
  $outputs = array();
  foreach ($hls_presets as $prefix => $preset_id) {
    array_push($outputs, array('Key' => "$prefix/$output_key", 'PresetId' => $preset_id, 'SegmentDuration' => $segment_duration));
  }
  
  // Setup master playlist which can be used to play using adaptive bitrate.
  $playlist = array(
    'Name' => 'hls_' . $output_key,
    'Format' => 'HLSv3',
    'OutputKeys' => array_map(function($x) { return $x['Key']; }, $outputs)
  );

  // Create the job.
  $create_job_request = array(
        'PipelineId' => $pipeline_id, 
        'Input' => $input, 
        'Outputs' => $outputs, 
        'OutputKeyPrefix' => "$output_key_prefix$output_key/", 
        'Playlists' => array($playlist)
  );
  $create_job_result = $transcoder_client->createJob($create_job_request)->toArray();
  return $job = $create_job_result['Job'];
}   

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
  // If the request method is GET, return the form which will allow the user to
  // specify pipeline and input key.
  echo "Create an Elastic Transcoder HLS job.<br><form action=\"http://ec2-23-22-149-62.compute-1.amazonaws.com/samples/HlsJobCreationSample.php\" method=\"POST\">Pipeline Id: <input name=\"pipelineid\" type=\"text\"/> (<a href=\"http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#php-pipeline\"> Create an Elastic Transcoder Pipeline</a>)<br>Input key: <input name=\"inputkey\" type=\"text\" /><br><input type=\"submit\" value=\"Create Job\" /></form>";
} else if ($_SERVER['REQUEST_METHOD'] == 'POST') {
  // If the request method is POST, create an HLS job using the posted data.
  $job = create_hls_job($transcoder_client, $_POST['pipelineid'], $_POST['inputkey'], $output_key_prefix, $hls_presets, $segment_duration);
  
  // Output the result.
  echo '<PRE>';
  echo "HLS job has been created:\n";
  echo json_encode($job, JSON_PRETTY_PRINT);
  echo '</PRE>';
}

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[HlsJobCreationSample.php demonstrates how to create an HLS job.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Elastic Transcoder]
// snippet-service:[elastictranscoder]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

?>
