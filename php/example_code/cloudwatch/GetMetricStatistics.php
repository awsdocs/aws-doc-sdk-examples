<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.get_metric_stats.complete]
// snippet-start:[cloudwatch.php.get_metric_stats.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.get_metric_stats.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Provides statistical information for a specified metric 
 * in Amazon CloudWatch.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $namespace: The metric's namespace.
 * - $metricName: The metric's name.
 * - $dimensions: Any required dimensions for the specified metric.
 * - $startTime: 
 * - $endTime:
 * - $period:
 * - $statistics:
 * - $unit: 
 * 
 * Returns: Statistical information for the specific metric;
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */
 
// snippet-start:[cloudwatch.php.get_metric_stats.main]
function getMetricStatistics($cloudWatchClient, $namespace, $metricName, 
    $dimensions, $startTime, $endTime, $period, $statistics, $unit)
{
    try {
        $result = $cloudWatchClient->getMetricStatistics([
            'Namespace' => $namespace,
            'MetricName' => $metricName,
            'Dimensions' => $dimensions,
            'StartTime' => $startTime,
            'EndTime' => $endTime,
            'Period' => $period,
            'Statistics' => $statistics,
            'Unit' => $unit
        ]);
        
        $message = '';

        if (count($result['Datapoints']) > 0)
        {
            $message .= "Datapoints found:\n";

            foreach($result['Datapoints'] as $datapoint)
            {
                $message .= $datapoint;
            }
        } else {
            $message .= 'No datapoints found for ' . $metricName . '.';
        }

        return $message;
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function getTheMetricStatistics()
{
    $namespace = 'AWS/S3';
    $metricName = 'BucketSizeBytes';
    $dimensions = [
        [
            'Name' => 'StorageTypes',
            'Value'=> 'StandardStorage'
        ],
        [
            'Name' => 'BucketName',
            'Value' => 'my-bucket-992648334831-2'
        ]
    ];
    $startTime = strtotime('-3 days');
    $endTime = strtotime('now');
    $period = 86400; // Seconds. (1 day = 86400 seconds.)
    $statistics = array('Average');
    $unit = 'Bytes';

    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo getMetricStatistics($cloudWatchClient, $namespace, $metricName, 
    $dimensions, $startTime, $endTime, $period, $statistics, $unit);
}

// Uncomment the following line to run this code in an AWS account.
// getTheMetricStatistics();
// snippet-end:[cloudwatch.php.get_metric_stats.main]
// snippet-end:[cloudwatch.php.get_metric_stats.complete]
// snippet-sourcedescription:[GetMetricStatistics.php demonstrates how to get statistics for a specified metric in Amazon CloudWatch.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-07]
// snippet-sourceauthor:[pccornel (AWS)]

