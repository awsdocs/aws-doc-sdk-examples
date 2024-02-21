<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[securityhub.php.delete_members.complete]
// snippet-start:[securityhub.php.delete_members.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[securityhub.php.delete_members.import]

// snippet-start:[securityhub.php.delete_members.main]
// Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

$awsAccounts = ['0123456789'];

try {
    $result = $client->deleteMembers([
        'AccountIds' => $awsAccounts,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[securityhub.php.delete_members.main]
// snippet-end:[securityhub.php.delete_members.complete]
