<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[securityhub.php.list_invitations.complete]
// snippet-start:[securityhub.php.list_invitations.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[securityhub.php.list_invitations.import]

// snippet-start:[securityhub.php.list_invitations.main]
// Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

try {
    $result = $client->listInvitations();
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[securityhub.php.list_invitations.main]
// snippet-end:[securityhub.php.list_invitations.complete]
