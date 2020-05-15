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
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudWatch\CloudWatchClient;

class SetAlarmStateTest extends TestCase
{
    public function testSetTheAlarmState()
    {
        require('./SetAlarmState.php');

        $alarmName = 'my-ec2-resources';
        $stateValue = 'OK';
        $stateReason = 'AWS SDK PHP example code set the state of the alarm ' . 
            $alarmName . ' to ' . $stateValue;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudWatchClient = new CloudWatchClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDWATCH_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = setAlarmState($cloudWatchClient, AWS_REGION, 
            $alarmName, $stateValue, $stateReason);

        $this->assertStringContainsString(
            'Successfully changed state of specified alarm.', $result);
    }
}