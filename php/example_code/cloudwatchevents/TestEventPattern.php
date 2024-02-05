<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudwatchevents.php.test_event_pattern.complete]
// snippet-start:[cloudwatchevents.php.test_event_pattern.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[cloudwatchevents.php.test_event_pattern.import]

/**
 * Test event pattern
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[cloudwatchevents.php.test_event_pattern.main]
$client = new Aws\cloudwatchevents\cloudwatcheventsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2015-10-07'
]);

$exampleEvent = '{
  "version": "0",
  "id": "6a7e8feb-b491-4cf7-a9f1-bf3703467718",
  "detail-type": "EC2 Instance State-change Notification",
  "source": "aws.ec2",
  "account": "111122223333",
  "time": "2015-12-22T18:43:48Z",
  "region": "us-east-2",
  "resources": [
    "arn:aws:ec2:us-east-2:123456789012:instance/i-12345678"
  ],
  "detail": {
    "instance-id": "i-12345678",
    "state": "terminated"
  }
}';

$exampleEventPattern = '{
  "source": [ "aws.ec2" ],
  "detail-type": [ "EC2 Instance State-change Notification" ],
  "detail": {
    "state": [ "terminated" ]
  }
}';

try {
    $result = $client->testEventPattern([
        'Event' => $exampleEvent,
        'EventPattern' => $exampleEventPattern
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[cloudwatchevents.php.test_event_pattern.main]
// snippet-end:[cloudwatchevents.php.test_event_pattern.complete]
