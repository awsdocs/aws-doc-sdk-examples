<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SetAlarmState.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudwatch-setalarmstate
*/

namespace Cloudwatch;

use Aws\CloudWatch\CloudWatchClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class SetAlarmStateTest extends TestCase
{
    public function testSetTheAlarmState()
    {
        require(__DIR__ . '/../SetAlarmState.php');

        $alarmName = 'my-ec2-resources';
        $stateValue = 'OK';
        $stateReason = 'AWS SDK for PHP example code set the state of the alarm ' .
            $alarmName . ' to ' . $stateValue;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = setAlarmState(
            $cloudWatchClient,
            AWS_REGION,
            $alarmName,
            $stateValue,
            $stateReason
        );

        $this->assertStringContainsString(
            'Successfully changed state of specified alarm.',
            $result
        );
    }
}
