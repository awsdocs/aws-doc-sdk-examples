<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Tables_CRUD]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\DynamoDb\Exception\DynamoDbException;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableName = 'ExampleTable';

echo "# Creating table $tableName...\n";

try {
    $response = $dynamodb->createTable([
        'TableName' => $tableName,
        'AttributeDefinitions' => [
            [
                'AttributeName' => 'Id',
                'AttributeType' => 'N'
            ]
        ],
        'KeySchema' => [
            [
                'AttributeName' => 'Id',
                'KeyType' => 'HASH'  //Partition key.
            ]
        ],
        'BillingMode' => 'PAY_PER_REQUEST'  // Use on-demand billing mode.
    ]);

    $dynamodb->waitUntil('TableExists', [
        'TableName' => $tableName,
        '@waiter' => [
            'delay'       => 5,
            'maxAttempts' => 20
        ]
    ]);

    print_r($response->getPath('TableDescription'));

    echo "table $tableName has been created.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

####################################################################
# Updating the table

// No need to update provisioned throughput as we are using PAY_PER_REQUEST mode.

echo "# Skipping table update for throughput settings (not needed for PAY_PER_REQUEST).\n";

####################################################################
# Deleting the table

try {
    echo "# Deleting table $tableName...\n";

    $response = $dynamodb->deleteTable([ 'TableName' => $tableName]);

    $dynamodb->waitUntil('TableNotExists', [
        'TableName' => $tableName,
        '@waiter' => [
            'delay'       => 5,
            'maxAttempts' => 20
        ]
    ]);
    echo "The table has been deleted.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to delete table $tableName\n");
}

####################################################################
# List all table names for this AWS account, in this region

echo "# Listing all of your tables in the current region...\n";

$tables = [];

// Walk through table names, two at a time

unset($response);

do {
    if (isset($response)) {
        $params = [
            'Limit' => 2,
            'ExclusiveStartTableName' => $response['LastEvaluatedTableName']
        ];
    } else {
        $params = ['Limit' => 2];
    }

    $response = $dynamodb->listTables($params);

    foreach ($response['TableNames'] as $key => $value) {
        echo "$value\n";
    }

    $tables = array_merge($tables, $response['TableNames']);
} while ($response['LastEvaluatedTableName']);

// Print total number of tables

echo "Total number of tables: ";
print_r(count($tables));
echo "\n";

// snippet-end:[dynamodb.php.codeexample.Tables_CRUD]
