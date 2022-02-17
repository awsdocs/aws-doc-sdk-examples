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
// snippet-start:[elastictranscoder.php.create_job_status_notification.import] 
require 'path/to/autoload.php';

use Aws\ElasticTranscoder\ElasticTranscoderClient;

$tmp_path = '/tmp';

// This will generate a 480p 16:9 mp4 output.
$preset_id = '1351620000001-000020';

// All inputs will have this prefix prepended to their input key.
$input_key_prefix = 'elastic-transcoder-samples/input/';

// All outputs will have this prefix prepended to their output key.
$output_key_prefix = 'elastic-transcoder-samples/output/';

// Region where you setup your AWS resources.
$region = 'us-east-1';

// Create the client for Elastic Transcoder.
$transcoder_client = ElasticTranscoderClient::factory(array('region' => $region, 'default_caching_config' => '/tmp'));

// This function will create an Elastic Transcoder job using the supplied elastic
// transcoder client, pipeline id, input key and output key prefix.  Output key
// is automatically set to SHA256(UTF8(<input-key>)).
function create_elastic_transcoder_job($transcoder_client, $pipeline_id, $input_key, $preset_id, $output_key_prefix) {
  // Setup the job input using the provided input key.
  $input = array('Key' => $input_key);
  
  // Setup the job output using the provided input key to generate an output key.
  $outputs = array(array('Key' => hash("sha256", utf8_encode($input_key)), 'PresetId' => $preset_id));
  
  // Create the job.
  $create_job_request = array(
        'PipelineId' => $pipeline_id,
        'Input' => $input,
        'Outputs' => $outputs,
        'OutputKeyPrefix' => $output_key_prefix
  );
  $create_job_result = $transcoder_client->createJob($create_job_request)->toArray();
  return $job = $create_job_result["Job"];
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
  // If the request method is POST, create a job using the posted data and save
  // the job information into the status file.
  try {
    $job = create_elastic_transcoder_job($transcoder_client, $_POST['pipelineid'], $_POST['inputkey'], $preset_id, $output_key_prefix);
    $status_file = "$tmp_path/{$job['Id']}";
    file_put_contents($status_file, json_encode($job, true) . "\n", FILE_APPEND | LOCK_EX);
    header("Location: JobStatusNotificationsSample.php?Id={$job['Id']}");
  } catch (Exception $e) {
    echo "Exception occurred: {$e->getMessage()}\n";
  }
} else if ($_SERVER['REQUEST_METHOD'] && array_key_exists('Id', $_GET)) {
  // If the request method is GET and 'Id' has been specified, then set the status file.
  $status_file = "$tmp_path/{$_GET['Id']}";
  if (is_file($status_file)) {
    echo '<pre>';
    echo file_get_contents($status_file);
    echo '</pre>';
  } else {
    echo "No job status file found.";
  }
} else if ($_SERVER['REQUEST_METHOD'] == 'GET'){
  // If the request method is GET and no 'Id' is specified, return the HTML form
  // which will allow the user to create an elastic transcoder job.
  echo "Create an Elastic Transcoder job and consume job status using notifications.<br>"
  echo "<form action=\"JobStatusNotificationsSample.php\" "
  echo "method=\"POST\">Pipeline Id: <input name=\"pipelineid\" type=\"text\"/> (<a href=\""
  echo "http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#php-pipeline\">"
  echo "Create an Elastic Transcoder Pipeline</a>)<br>Input key: <input name=\"inputkey\" type=\"text\" /><br>"
  echo "<input type=\"submit\" value=\"Create Job\" /></form>";
}
// snippet-end:[elastictranscoder.php.create_job_status_notification.import]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[JobStatusNotificationSample.php demonstrates how to create an Elastic Transcoder job and consume job status using notifications.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Elastic Transcoder]
// snippet-service:[elastictranscoder]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

?>
