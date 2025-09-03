<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


/*
Relies on PHPUnit to test the functionality in ./DescribeAlarmsForMetric.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-describealarmsformetric
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class DescribeAlarmsForMetricTest extends TestCase
{
    public function testDescribesAlarmsForAMetric()
    {
        require(__DIR__ . '/../DescribeAlarmsForMetric.php');

        $metricName = CLOUDWATCH_METRIC_NAME;
        $namespace = CLOUDWATCH_METRIC_NAMESPACE;
        $dimensions = [
            [
                'Name' => 'StorageTypes',
                'Value' => 'StandardStorage'
            ],
            [
                'Name' => 'BucketName',
                'Value' => 'amzn-s3-demo-bucket'
            ]
        ];

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = describeAlarmsForMetric(
            $cloudWatchClient,
            $metricName,
            $namespace,
            $dimensions
        );

        $this->assertStringContainsString('https://monitoring.' . AWS_REGION .
            '.amazonaws.com', $result);
    }
}
