<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *
 */
// snippet-start:[cloudwatch.php.set_alarm.complete]
// snippet-start:[cloudwatch.php.set_alarm.import]

require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.set_alarm.import]

/**
 * Set Alarm State in CloudWatch
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

$alarmName = "<ALARM_NAME>";
 
// snippet-start:[cloudwatch.php.set_alarm.main]
$client = new Aws\CloudWatch\CloudWatchClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-08-01'
]);

try {
    $result = $client->setAlarmState(array(
        'AlarmName' => $alarmName, //REQUIRED
        'StateValue' => 'OK', //REQUIRED. Allowed Values : OK | ALARM | INSUFFICIENT_DATA
        'StateReason' => 'Testing Sample Code' //REQUIRED
    ));
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
 
 
// snippet-end:[cloudwatch.php.set_alarm.main]
// snippet-end:[cloudwatch.php.set_alarm.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SetAlarmState.php demonstrates how to set Alarm State in Amazon CloudWatch.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-27]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

