<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[securityhub.php.disable_securityhub.complete]
// snippet-start:[securityhub.php.disable_securityhub.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[securityhub.php.disable_securityhub.import]

// snippet-start:[securityhub.php.disable_securityhub.main]
// Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

try {
    $result = $client->disableSecurityHub();
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[securityhub.php.disable_securityhub.main]
// snippet-end:[securityhub.php.disable_securityhub.complete]
