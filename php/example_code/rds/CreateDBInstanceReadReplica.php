<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.rds.createDBInstanceReadReplica.complete]
// snippet-start:[php.example_code.rds.createDBInstanceReadReplica.import]

require __DIR__ . '/vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[php.example_code.rds.createDBInstanceReadReplica.import]

// snippet-start:[php.example_code.rds.createDBInstanceReadReplica.main]

$rdsClient = new Aws\Rds\RdsClient([
    'region' => 'us-east-2'
]);

$replicaDBIdentifier = '<<{{name-for-db-replica}}>>';
$sourceDBIdentifier = '<<{{db-identifier-of-db-to-replicate}}>>';

try {
    $result = $rdsClient->createDBInstanceReadReplica([
        'DBInstanceIdentifier' => $replicaDBIdentifier,
        'SourceDBInstanceIdentifier' => $sourceDBIdentifier,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[php.example_code.rds.createDBInstanceReadReplica.main]
// snippet-end:[php.example_code.rds.createDBInstanceReadReplica.complete]
