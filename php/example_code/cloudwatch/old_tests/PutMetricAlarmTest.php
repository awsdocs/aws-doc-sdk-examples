<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./PutMetricAlarm.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-putmetricalarm
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class PutMetricAlarmTest extends TestCase
{
    public function testSetAMetricInAlarm()
    {
        require(__DIR__ . '/../PutMetricAlarm.php');

        $alarmName = CLOUDWATCH_ALARM_NAME;
        $namespace = CLOUDWATCH_METRIC_NAMESPACE_USAGE;
        $metricName = CLOUDWATCH_METRIC_NAME_USAGE;
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
        $statistic = CLOUDWATCH_METRIC_STATISTICS;
        $period = 300;
        $comparison = 'GreaterThanThreshold';
        $threshold = 1;
        $evaluationPeriods = 1;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = putMetricAlarm(
            $cloudWatchClient,
            AWS_REGION,
            $alarmName,
            $namespace,
            $metricName,
            $dimensions,
            $statistic,
            $period,
            $comparison,
            $threshold,
            $evaluationPeriods
        );

        $this->assertStringContainsString(
            'Successfully created or updated specified alarm.',
            $result
        );
    }
}
