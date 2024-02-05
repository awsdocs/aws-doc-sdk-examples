<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Scan_PopulateTable]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\DynamoDb\Exception\DynamoDbException;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableName = 'ProductCatalog';

// Delete an old DynamoDB table

echo "Deleting the table...\n";

try {
    $response = $dynamodb->deleteTable([
        'TableName' => $tableName
    ]);

    $dynamodb->waitUntil('TableNotExists', [
        'TableName' => $tableName,
        '@waiter' => [
            'delay'       => 5,
            'maxAttempts' => 20
        ]
    ]);
    echo "The table has been deleted.\n";

    echo "The table {$tableName} has been deleted.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to delete table $tableName\n");
}

// Create a new DynamoDB table

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
                'KeyType'       => 'HASH'  //Partition key
            ]
        ],
        'ProvisionedThroughput' => [
            'ReadCapacityUnits'  => 5,
            'WriteCapacityUnits' => 6
        ]
    ]);

    $dynamodb->waitUntil('TableExists', [
         'TableName' => $tableName,
         '@waiter' => [
             'delay'       => 5,
             'maxAttempts' => 20
         ]
    ]);
    echo "Table $tableName has been created.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

// Populate DynamoDB table

echo "# Populating Items to $tableName...\n";

for ($i = 1; $i <= 100; $i++) {
    $response = $dynamodb->putItem([
        'TableName' => $tableName,
        'Item' => [
            'Id'      => [ 'N'     => "$i" ], // Primary Key
            'Title'   => [ 'S'     => "Book $i Title" ],
            'ISBN'    => [ 'S'     => '111-1111111111' ],
            'Price'   => [ 'N'     => "25" ],
            'Authors' => [ 'SS' => ['Author1', 'Author2']]
        ]
    ]);

    $response = $dynamodb->getItem([
        'TableName' => 'ProductCatalog',
        'Key' => [
            'Id' => [ 'N' => "$i" ]
        ]
    ]);

    echo "Item populated: {$response['Item']['Title']['S']}\n";
    sleep(1);
}

echo "$tableName is populated with items.\n";

// snippet-end:[dynamodb.php.codeexample.Scan_PopulateTable]
