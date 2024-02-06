<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[polly.php.list_lexicon.complete]
// snippet-start:[polly.php.list_lexicon.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[polly.php.list_lexicon.import]

/**
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[polly.php.list_lexicon.main]
// Create a PollyClient
$client = new Aws\Polly\PollyClient([
    'profile' => 'default',
    'version' => '2016-06-10',
    'region' => 'us-east-2'
]);

try {
    $result = $client->listLexicons();
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[polly.php.list_lexicon.main]
// snippet-end:[polly.php.list_lexicon.complete]
