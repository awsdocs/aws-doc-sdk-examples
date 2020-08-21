<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.set_alarm.complete]
// snippet-start:[cloudwatch.php.set_alarm.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.set_alarm.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Sets the state of the specified alarm in Amazon CloudWatch.
 * 
 * Prerequisites: At least one CloudWatch alarm.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized CloudWatch client.
 * - $cloudWatchRegion: The alarm's AWS Region.
 * - $alarmName: The alarm's name.
 * - $stateValue: The alarm's new state.
 * - $stateReason: The reason for the alarm.
 * 
 * Returns: Information about the alarm's state change request; 
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudwatch.php.set_alarm.main]
function setAlarmState($cloudWatchClient, $cloudWatchRegion, $alarmName, 
    $stateValue, $stateReason)
{
    try {
        $result = $cloudWatchClient->setAlarmState([
            'AlarmName' => $alarmName,
            'StateValue' => $stateValue,
            'StateReason' => $stateReason
        ]);

        if (isset($result['@metadata']['effectiveUri']))
        {
            if ($result['@metadata']['effectiveUri'] == 
                'https://monitoring.' . $cloudWatchRegion . '.amazonaws.com')
            {
                return 'Successfully changed state of specified alarm.';
            } else {
                return 'Could not change state of specified alarm.';
            }
        } else {
            return 'Could not change state of specified alarm.';
        }
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function setTheAlarmState()
{
    $alarmName = 'my-ec2-resources';
    $stateValue = 'OK';
    $stateReason = 'AWS SDK for PHP example code set the state of the alarm ' . 
        $alarmName . ' to ' . $stateValue;

    $cloudWatchRegion = 'us-east-1';
    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => $cloudWatchRegion,
        'version' => '2010-08-01'
    ]);

    echo setAlarmState($cloudWatchClient, $cloudWatchRegion, 
        $alarmName, $stateValue, $stateReason);
}

// Uncomment the following line to run this code in an AWS account.
// setTheAlarmState();
// snippet-end:[cloudwatch.php.set_alarm.main]
// snippet-end:[cloudwatch.php.set_alarm.complete]
// snippet-sourcedescription:[SetAlarmState.php demonstrates how to set the state of an alarm in Amazon CloudWatch.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-08]
// snippet-sourceauthor:[pccornel (AWS)]

