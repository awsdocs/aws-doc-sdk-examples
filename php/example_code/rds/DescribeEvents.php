<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.rds.describeEvents.complete]
// snippet-start:[php.example_code.rds.describeEvents.import]

require __DIR__ . '/vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[php.example_code.rds.describeEvents.import]

// snippet-start:[php.example_code.rds.describeEvents.main]

$rdsClient = new Aws\Rds\RdsClient([
    'region' => 'us-east-2'
]);

$dbIdentifier = '<<{{db-identifier}}>>';

try {
    $result = $rdsClient->describeEvents([
        'SourceIdentifier' => $dbIdentifier,
        'SourceType' => 'db-instance',
    ]);
    var_dump($result);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[php.example_code.rds.describeEvents.main]
// snippet-end:[php.example_code.rds.describeEvents.complete]
