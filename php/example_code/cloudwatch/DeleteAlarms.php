<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.delete_alarm.complete]
// snippet-start:[cloudwatch.php.delete_alarm.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.delete_alarm.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Deletes an existing Amazon CloudWatch alarm.
 *
 * Prerequisites: At least one existing CloudWatch alarm.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $alarmNames: An array of names of the alarms to delete.
 * 
 * Returns: Information about the deletion request; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudwatch.php.delete_alarm.main]
function deleteAlarms($cloudWatchClient, $alarmNames)
{
    try {
        $result = $cloudWatchClient->deleteAlarms([
            'AlarmNames' => $alarmNames
        ]);
        return 'The specified alarms at the following effective URI have ' . 
            'been deleted or do not currently exist: ' . 
            $result['@metadata']['effectiveUri'];
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function deleteTheAlarms()
{
    $alarmNames = array('my-alarm');
    
    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo deleteAlarms($cloudWatchClient, $alarmNames);
}

// Uncomment the following line to run this code in an AWS account.
deleteTheAlarms();
// snippet-end:[cloudwatch.php.delete_alarm.main]
// snippet-end:[cloudwatch.php.delete_alarm.complete]
// snippet-sourcedescription:[DeleteAlarms.php demonstrates how to delete one or more Amazon CloudWatch alarms given the alarm names.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-05]
// snippet-sourceauthor:[pccornel (AWS)]

