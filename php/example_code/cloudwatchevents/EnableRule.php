<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudwatchevents.php.enable_rule.complete]
// snippet-start:[cloudwatchevents.php.enable_rule.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[cloudwatchevents.php.enable_rule.import]

/**
 * Enable Rule
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[cloudwatchevents.php.enable_rule.main]
$client = new Aws\cloudwatchevents\cloudwatcheventsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2015-10-07'
]);

try {
    $result = $client->enableRule([
        'Name' => 'DEMO_EVENT', // REQUIRED
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[cloudwatchevents.php.enable_rule.main]
// snippet-end:[cloudwatchevents.php.enable_rule.complete]
