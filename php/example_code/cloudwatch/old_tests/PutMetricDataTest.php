<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./PutMetricData.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-putmetricdata
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class PutMetricDataTest extends TestCase
{
    public function testSetDataForAMetric()
    {
        require(__DIR__ . '/../PutMetricData.php');

        $namespace = 'MyNamespace';
        $metricData = [
            [
                'MetricName' => 'MyMetric',
                'Timestamp' => 1589228818, // 11 May 2020, 20:26:58 UTC.
                'Dimensions' => [
                    [
                        'Name' => 'MyDimension1',
                        'Value' => 'MyValue1'

                    ],
                    [
                        'Name' => 'MyDimension2',
                        'Value' => 'MyValue2'
                    ]
                ],
                'Unit' => 'Count',
                'Value' => 1
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

        $result = putMetricData(
            $cloudWatchClient,
            AWS_REGION,
            $namespace,
            $metricData
        );

        $this->assertStringContainsString(
            'Successfully published datapoint(s).',
            $result
        );
    }
}
