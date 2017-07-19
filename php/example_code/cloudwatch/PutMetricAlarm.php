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
 * Put Metric Alarm
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$client = new CloudWatchClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-08-01'
]);

try {
    $result = $client->putMetricAlarm(array(
        // AlarmName is required
        'AlarmName' => 'string',
        // MetricName is required
        'MetricName' => 'string',
        // Namespace is required
        'Namespace' => 'string',
        // Statistic is required
        //string: SampleCount | Average | Sum | Minimum | Maximum
        'Statistic' => 'string',
        // Period is required
        'Period' => integer,
        'Unit' => 'Count/Second',
        // EvaluationPeriods is required
        'EvaluationPeriods' => integer,
        // Threshold is required
        'Threshold' => interger,
        // ComparisonOperator is required
        // string: GreaterThanOrEqualToThreshold | GreaterThanThreshold | LessThanThreshold | LessThanOrEqualToThreshold
        'ComparisonOperator' => 'string',
    ));
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
