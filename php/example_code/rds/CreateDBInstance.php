<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.rds.createDBInstance.complete]
// snippet-start:[php.example_code.rds.createDBInstance.import]

require __DIR__ . '/vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[php.example_code.rds.createDBInstance.import]

// snippet-start:[php.example_code.rds.createDBInstance.main]

$rdsClient = new Aws\Rds\RdsClient([
    'region' => 'us-east-2'
]);

$dbIdentifier = '<<{{db-identifier}}>>';
$dbClass = 'db.t2.micro';
$storage = 5;
$engine = 'MySQL';
$username = 'MyUser';
$password = 'MyPassword';

try {
    $result = $rdsClient->createDBInstance([
        'DBInstanceIdentifier' => $dbIdentifier,
        'DBInstanceClass' => $dbClass,
        'AllocatedStorage' => $storage,
        'Engine' => $engine,
        'MasterUsername' => $username,
        'MasterUserPassword' => $password,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[php.example_code.rds.createDBInstance.main]
// snippet-end:[php.example_code.rds.createDBInstance.complete]
