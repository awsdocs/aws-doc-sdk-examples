<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.disable_alarms_actions.complete]
// snippet-start:[cloudwatch.php.disable_alarms_actions.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.disable_alarms_actions.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Disables actions for specified alarms in Amazon CloudWatch.
 * 
 * Prerequisites: At least one existing CloudWatch alarm.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudWatch.
 * - $alarmNames: The names of the alarms to disable actions for.
 * 
 * Returns: Information about the results of the request;
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

 // snippet-start:[cloudwatch.php.disable_alarms_actions.main]
function disableAlarmActions($cloudWatchClient, $alarmNames)
{
    try {
        $result = $cloudWatchClient->disableAlarmActions([
            'AlarmNames' => $alarmNames
        ]);

        if (isset($result['@metadata']['effectiveUri']))
        {
            return 'At the effective URI of ' . 
                $result['@metadata']['effectiveUri'] . 
                ', actions for any matching alarms have been disabled.';
        } else {
            return 'Actions for some matching alarms ' . 
                'might not have been disabled.';
        }

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function disableTheAlarmActions()
{
    $alarmNames = array('my-alarm');
 
    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo disableAlarmActions($cloudWatchClient, $alarmNames);
}

// Uncomment the following line to run this code in an AWS account.
// disableTheAlarmActions();
// snippet-end:[cloudwatch.php.disable_alarms_actions.main]
// snippet-end:[cloudwatch.php.disable_alarms_actions.complete]
// snippet-sourcedescription:[DisableAlarmActions.php demonstrates how to disable actions for specified AWS CloudWatch alarms.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-07]
// snippet-sourceauthor:[pccornel (AWS)]

