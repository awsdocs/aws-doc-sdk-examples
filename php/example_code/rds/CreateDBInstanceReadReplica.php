<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rds.php.create_db_replica.complete]
// snippet-start:[rds.php.create_db_replica.import]

require 'vendor/autoload.php';

use Aws\Rds\RdsClient; 
use Aws\Exception\AwsException;
// snippet-end:[rds.php.create_db_replica.import]

// snippet-start:[rds.php.create_db_replica.main]
//Create a RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-2'
]);

$replicadbIdentifier = '<<{{name-for-db-replica}}>>';
$sourcedbIdentifier = '<<{{db-identifier-of-db-to-replicate}}>>';

try {
    $result = $rdsClient->createDBInstanceReadReplica([
        'DBInstanceIdentifier' => $replicadbIdentifier,
        'SourceDBInstanceIdentifier' => $sourcedbIdentifier,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
} 
// snippet-end:[rds.php.create_db_replica.main]
// snippet-end:[rds.php.create_db_replica.complete]

