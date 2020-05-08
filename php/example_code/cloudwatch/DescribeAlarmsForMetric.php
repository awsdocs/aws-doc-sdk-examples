<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.describe_alarms_metric.complete]
// snippet-start:[cloudwatch.php.describe_alarms_metric.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.describe_alarms_metric.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Provides a list of alarms that match the specified 
 * metric in Amazon CloudWatch.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $metricName: The name of the metric, for example BucketSizeBytes.
 * - $namespace: The related namespace for the metric, for example AWS/S3.
 * - $dimensions: Any related dimensions, if the metric requires them to
 *   be specified.
 * 
 * Returns: Information about any matching alarms found; 
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */
 
// snippet-start:[cloudwatch.php.describe_alarms_metric.main]
function describeAlarmsForMetric($cloudWatchClient, $metricName, 
    $namespace, $dimensions)
{
    try {
        $result = $cloudWatchClient->describeAlarmsForMetric([
            'MetricName' => $metricName,
            'Namespace' => $namespace,
            'Dimensions' => $dimensions
        ]);

        $message = '';

        if (count($result['MetricAlarms']) > 0)
        {
            $message .= 'Matching alarms for ' . $metricName . ":\n";

            foreach ($result['MetricAlarms'] as $alarm)
            {
                $message .= $alarm['AlarmName'] . "\n";
            }
        } else {
            $message .= 'No matching alarms found for ' . $metricName . ".";
        }

        return $message;
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function describeTheAlarmsForMetric()
{
    $metricName = 'BucketSizeBytes';
    $namespace = 'AWS/S3';
    $dimensions = [
        [
            'Name' => 'StorageTypes',
            'Value'=> 'StandardStorage'
        ],
        [
            'Name' => 'BucketName',
            'Value' => 'my-bucket'
        ]
    ];

    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo describeAlarmsForMetric($cloudWatchClient, $metricName, 
        $namespace, $dimensions);
}

// Uncomment the following line to run this code in an AWS account.
// describeTheAlarmsForMetric();
// snippet-end:[cloudwatch.php.describe_alarms_metric.main]
// snippet-end:[cloudwatch.php.describe_alarms_metric.complete]
// snippet-sourcedescription:[DescribeAlarmsForMetric.php demonstrates how to retrieves the alarms for the specified AWS CloudWatch metric.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-06]
// snippet-sourceauthor:[pccornel (AWS)]

