<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./GetMetricStatistics.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-getmetricstatistics
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class GetMetricStatisticsTest extends TestCase
{
    public function testGetStatisticsForAMetric()
    {
        require(__DIR__ . '/../GetMetricStatistics.php');

        $namespace = CLOUDWATCH_METRIC_NAMESPACE_USAGE;
        $metricName = CLOUDWATCH_METRIC_NAME_USAGE;
        $dimensions = [
            [
                'Name' => 'Service',
                'Value' => 'EC2'
            ],
            [
                'Name' => 'Resource',
                'Value' => 'vCPU'
            ],
            [
                'Name' => 'Type',
                'Value' => 'Resource'
            ],
            [
                'Name' => 'Class',
                'Value' => 'Standard/OnDemand'
            ]
        ];
        $startTime = strtotime(CLOUDWATCH_METRIC_START_TIME);
        $endTime = strtotime(CLOUDWATCH_METRIC_END_TIME);
        $period = 300;
        $statistics = array(CLOUDWATCH_METRIC_STATISTICS);
        $unit = CLOUDWATCH_METRIC_UNIT;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = getMetricStatistics(
            $cloudWatchClient,
            $namespace,
            $metricName,
            $dimensions,
            $startTime,
            $endTime,
            $period,
            $statistics,
            $unit
        );

        $this->assertStringContainsString('https://monitoring.' . AWS_REGION .
            '.amazonaws.com', $result);
    }
}
