<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rds.php.delete_instance.complete]
// snippet-start:[rds.php.delete_instance.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[rds.php.delete_instance.import]
// snippet-start:[rds.php.delete_instance.main]
//Create a RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-1'
]);

$dbIdentifier = '<<{{db-identifier}}>>';

try {
    $result = $rdsClient->deleteDBInstance([
        'DBInstanceIdentifier' => $dbIdentifier,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[rds.php.delete_instance.main]
// snippet-end:[rds.php.delete_instance.complete]
