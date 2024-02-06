<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.rds.startDBInstance.complete]
// snippet-start:[php.example_code.rds.startDBInstance.import]

require __DIR__ . '/vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[php.example_code.rds.startDBInstance.import]

// snippet-start:[php.example_code.rds.startDBInstance.main]
//Create an RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-2'
]);

$dbIdentifier = '<<{{db-identifier}}>>';

try {
    $result = $rdsClient->startDBInstance([
        'DBInstanceIdentifier' => $dbIdentifier,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[php.example_code.rds.startDBInstance.main]
// snippet-end:[php.example_code.rds.startDBInstance.complete]
