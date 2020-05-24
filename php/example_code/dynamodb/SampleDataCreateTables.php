<?php
// snippet-sourcedescription:[SampleDataCreateTables.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.SampleDataCreateTables] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

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
    exit ("Unable to create table $tableName\n");
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
    exit ("Unable to create table $tableName\n");
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
    exit ("Unable to create table $tableName\n");
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
    exit ("Unable to create table $tableName\n");
}



// snippet-end:[dynamodb.php.codeexample.SampleDataCreateTables] 
?>