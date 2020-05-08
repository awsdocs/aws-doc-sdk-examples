<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.enable_alarm.complete]
// snippet-start:[cloudwatch.php.enable_alarm.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.enable_alarm.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Enables actions for specified alarms in Amazon CloudWatch.
 * 
 * Prerequisites: At least one existing CloudWatch alarm.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $alarmNames: The names of the alarms to enable actions for.
 * 
 * Returns: Information about the results of the request;
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudwatch.php.enable_alarm.main]
function enableAlarmActions($cloudWatchClient, $alarmNames)
{
    try {
        $result = $cloudWatchClient->enableAlarmActions([
            'AlarmNames' => $alarmNames
        ]);
        
        $message = '';

        if (isset($result['@metadata']['effectiveUri']))
        {
            $message .= 'Actions for any matching alarms have been enabled.';
        } else {
            $message .= 'Actions for some matching alarms ' . 
                'might not have been enabled.';
        }
        
        return $message;
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function enableTheAlarmActions()
{
    $alarmNames = array('my-alarm');

    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo enableAlarmActions($cloudWatchClient, $alarmNames);
}

// Uncomment the following line to run this code in an AWS account.
enableTheAlarmActions();
// snippet-end:[cloudwatch.php.enable_alarm.main]
// snippet-end:[cloudwatch.php.enable_alarm.complete]
// snippet-sourcedescription:[EnableAlarmActions.php demonstrates how to enable actions for specified Amazon CloudWatch alarms.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-07]
// snippet-sourceauthor:[pccornel (AWS)]

