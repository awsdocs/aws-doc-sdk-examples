<?php
/**
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient;
use Aws\Exception\AwsException;

/**
 * Get Metric Statistics
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$client = CloudWatchClient::factory([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-08-01'
]);

try {
    $result = $client->getMetricStatistics(array(
        'Namespace' => 'string',
        'MetricName' => 'CloudWatchTests',
        //StartTime : mixed type: string (date format)|int (unix timestamp)|\DateTime
        'StartTime' => strtotime('-1 days'),
        //EndTime : mixed type: string (date format)|int (unix timestamp)|\DateTime
        'EndTime' => strtotime('now'),
        //The granularity, in seconds, of the returned datapoints. Period must be at least 60 seconds and must be a multiple of 60. The default value is 60
        'Period' => 3000,
        'Statistics' => array('Maximum', 'Minimum'),
    ));
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
