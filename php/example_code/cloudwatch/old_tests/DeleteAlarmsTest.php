<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./DeleteAlarms.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-deletealarms
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class DeleteAlarmsTest extends TestCase
{
    public function testDeletesAnAlarm()
    {
        require(__DIR__ . '/../DeleteAlarms.php');

        $alarmNames = array(CLOUDWATCH_ALARM_NAME);

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = deleteAlarms($cloudWatchClient, $alarmNames);

        $this->assertStringContainsString('https://monitoring.' . AWS_REGION .
            '.amazonaws.com', $result);
    }
}
