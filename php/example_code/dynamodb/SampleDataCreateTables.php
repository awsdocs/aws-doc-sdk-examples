<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.SampleDataCreateTables]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\DynamoDb\Exception\DynamoDbException;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableName = 'ProductCatalog';
echo "Creating table $tableName...";

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
                'KeyType' => 'HASH'  //Partition key
            ]
        ],
        'ProvisionedThroughput' => [
             'ReadCapacityUnits'    => 10,
             'WriteCapacityUnits' => 5
        ]
    ]);
    echo "CreateTable request was successful.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

$tableName = 'Forum';
echo "Creating table $tableName...";

try {
    $response = $dynamodb->createTable([
        'TableName' => $tableName,
        'AttributeDefinitions' => [
            [
                'AttributeName' => 'Name',
                'AttributeType' => 'S'
            ]
        ],
        'KeySchema' => [
            [
                'AttributeName' => 'Name',
                'KeyType' => 'HASH'  //Partition key
            ]
        ],
        'ProvisionedThroughput' => [
            'ReadCapacityUnits'    => 10,
            'WriteCapacityUnits' => 5
        ]
    ]);
    echo "CreateTable request was successful.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

$tableName = 'Thread';
echo "Creating table $tableName...";

try {
    $response = $dynamodb->createTable([
    'TableName' => $tableName,
    'AttributeDefinitions' => [
        [
            'AttributeName' => 'ForumName',
            'AttributeType' => 'S'
        ],
        [
            'AttributeName' => 'Subject',
            'AttributeType' => 'S'
        ]
    ],
    'KeySchema' => [
        [
            'AttributeName' => 'ForumName',
            'KeyType' => 'HASH'  //Partition key
        ],
        [
            'AttributeName' => 'Subject',
            'KeyType' => 'RANGE'  //Sort key
        ]
    ],
    'ProvisionedThroughput' => [
        'ReadCapacityUnits'    => 10,
        'WriteCapacityUnits' => 5
    ]
    ]);
    echo "CreateTable request was successful.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

$tableName = 'Reply';
echo "Creating table $tableName...";

try {
    $response = $dynamodb->createTable([
        'TableName' => $tableName,
        'AttributeDefinitions' => [
            [
                'AttributeName' => 'Id',
                'AttributeType' => 'S'
            ],
            [
                'AttributeName' => 'ReplyDateTime',
                'AttributeType' => 'S'
            ],
            [
                'AttributeName' => 'PostedBy',
                'AttributeType' => 'S'
            ]
        ],
        'LocalSecondaryIndexes' => [
            [
                'IndexName' => 'PostedBy-index',
                'KeySchema' => [
                    [
                        'AttributeName' => 'Id',
                        'KeyType' => 'HASH'  //Partition key
                    ],
                    [
                        'AttributeName' => 'PostedBy',
                        'KeyType' => 'RANGE'  //Sort key
                    ],
                ],
                'Projection' => [
                    'ProjectionType' => 'KEYS_ONLY',
                ],
            ],
        ],
        'KeySchema' => [
            [
                'AttributeName' => 'Id',
                'KeyType' => 'HASH'  //Partition key
            ],
            [
                'AttributeName' => 'ReplyDateTime',
                'KeyType' => 'RANGE'  //Sort key
            ]
        ],
        'ProvisionedThroughput' => [
            'ReadCapacityUnits'    => 10,
            'WriteCapacityUnits' => 5
        ]
    ]);
    echo "CreateTable request was successful.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit("Unable to create table $tableName\n");
}

// snippet-end:[dynamodb.php.codeexample.SampleDataCreateTables]
