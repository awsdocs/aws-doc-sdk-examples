<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ses-send-email.html
 *
 */
// snippet-start:[ses.php.send_quota.complete]
// snippet-start:[ses.php.send_quota.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Ses\SesClient;

// snippet-end:[ses.php.send_quota.import]

//Create a SESClient
// snippet-start:[ses.php.send_quota.main]
$SesClient = new SesClient([
    'profile' => 'default',
    'version' => '2010-12-01',
    'region' => 'us-east-1'

]);

try {
    $result = $SesClient->getSendQuota();
    $send_limit = $result["Max24HourSend"];
    $sent = $result["SentLast24Hours"];
    $available = $send_limit - $sent;
    print("<p>You can send " . $available . " more messages in the next 24 hours.</p>");
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[ses.php.send_quota.main]
// snippet-end:[ses.php.send_quota.complete]
