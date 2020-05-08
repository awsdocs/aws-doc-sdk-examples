<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.put_metric_alarm.complete]
// snippet-start:[cloudwatch.php.put_metric_alarm.import]

require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.put_metric_alarm.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Creates or updates an alarm for the specified metric 
 * in Amazon CloudWatch.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $cloudWatchRegion: The AWS Region for the new or updated alarm.
 * - $alarmName: The name of a new alarm to create or existing alarm to update.
 * - $namespace: The metric's namespace.
 * - $metricName: The metric's name.
 * - $dimensions: Any required dimensions for the specified metric.
 * - $statistic: The statistic for the specified metric.
 * - $period: The number of seconds between times the metric is evaluated.
 * - $comparison: The arithmetic operation to use when comparing the 
 * - $threshold: The value against which the specified statistic is compared.
 * - $evaluationPeriods: The number of periods over which data is compared to 
 *   the specified threshold.
 * 
 * Returns: Information about the new or updated alarm; 
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudwatch.php.put_metric_alarm.main]
function putMetricAlarm($cloudWatchClient, $cloudWatchRegion, 
    $alarmName, $namespace, $metricName, 
    $dimensions, $statistic, $period, $comparison, $threshold, 
    $evaluationPeriods)
{
    try {
        $result = $cloudWatchClient->putMetricAlarm([
            'AlarmName' => $alarmName,
            'Namespace' => $namespace,
            'MetricName' => $metricName,
            'Dimensions' => $dimensions,
            'Statistic' => $statistic,
            'Period' => $period,
            'ComparisonOperator' => $comparison,
            'Threshold' => $threshold,
            'EvaluationPeriods' => $evaluationPeriods
        ]);
        
        if (isset($result['@metadata']['effectiveUri']))
        {
            if ($result['@metadata']['effectiveUri'] == 
                'https://monitoring.' . $cloudWatchRegion . '.amazonaws.com')
            {
                return 'Successfully created or updated specified alarm.';
            } else {
                return 'Could not create or update specified alarm.';
            }
        } else {
            return 'Could not create or update specified alarm.';
        }
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function putTheMetricAlarm()
{
    $alarmName = 'my-ec2-resources';
    $namespace = 'AWS/Usage';
    $metricName = 'ResourceCount';
    $dimensions = [
        [
            'Name' => 'Type',
            'Value' => 'Resource'
        ],
        [
            'Name' => 'Resource',
            'Value' => 'vCPU'
        ],
        [
            'Name' => 'Service',
            'Value' => 'EC2'
        ],
        [
            'Name' => 'Class',
            'Value' => 'Standard/OnDemand'
        ]
    ];
    $statistic = 'Average';
    $period = 300;
    $comparison = 'GreaterThanThreshold';
    $threshold = 1;
    $evaluationPeriods = 1;

    $cloudWatchRegion = 'us-east-1';
    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => $cloudWatchRegion,
        'version' => '2010-08-01'
    ]);

    echo putMetricAlarm($cloudWatchClient, $cloudWatchRegion, 
        $alarmName, $namespace, $metricName, 
        $dimensions, $statistic, $period, $comparison, $threshold, 
        $evaluationPeriods);
}

// Uncomment the following line to run this code in an AWS account.
putTheMetricAlarm();
// snippet-end:[cloudwatch.php.put_metric_alarm.main]
// snippet-end:[cloudwatch.php.put_metric_alarm.complete]
// snippet-sourcedescription:[PutMetricAlarm.php demonstrates how to create or update an alarm and associate it with the specified AWS CloudWatch metric.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-08]
// snippet-sourceauthor:[pccornel (AWS)]
