<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./DescribeAlarmHistory.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-describealarmhistory
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class DescribeAlarmHistoryTest extends TestCase
{
    public function testDescribesTheAlarmHistory()
    {
        require(__DIR__ . '/../DescribeAlarmHistory.php');

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = describeAlarmHistory(
            $cloudWatchClient,
            CLOUDWATCH_ALARM_NAME
        );

        $this->assertStringContainsString('https://monitoring.' . AWS_REGION .
            '.amazonaws.com', $result);
    }
}
