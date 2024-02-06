<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.rds.describeDBInstances.complete]
// snippet-start:[php.example_code.rds.describeDBInstances.import]

require __DIR__ . '/vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[php.example_code.rds.describeDBInstances.import]

// snippet-start:[php.example_code.rds.describeDBInstances.main]
//Create an RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'region' => 'us-east-2'
]);

try {
    $result = $rdsClient->describeDBInstances();
    foreach ($result['DBInstances'] as $instance) {
        print('<p>DB Identifier: ' . $instance['DBInstanceIdentifier']);
        print('<br />Endpoint: ' . $instance['Endpoint']["Address"]
            . ':' . $instance['Endpoint']["Port"]);
        print('<br />Current Status: ' . $instance["DBInstanceStatus"]);
        print('</p>');
    }
    print(" Raw Result ");
    var_dump($result);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[php.example_code.rds.describeDBInstances.main]
// snippet-end:[php.example_code.rds.describeDBInstances.complete]
