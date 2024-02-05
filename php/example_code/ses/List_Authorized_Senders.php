<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ses-sender-policy.html
 *
 */
// snippet-start:[ses.php.list_authorized_senders.complete]
// snippet-start:[ses.php.list_authorized_senders.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Ses\SesClient;

// snippet-end:[ses.php.list_authorized_senders.import]

//Create a SESClient
// snippet-start:[ses.php.list_authorized_senders.main]
$SesClient = new SesClient([
    'profile' => 'default',
    'version' => '2010-12-01',
    'region' => 'us-east-1'
]);

$identity = "arn:aws:ses:us-east-1:123456789012:identity/example.com";

try {
    $result = $SesClient->listIdentityPolicies([
        'Identity' => $identity,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[ses.php.list_authorized_senders.main]
// snippet-end:[ses.php.list_authorized_senders.complete]
