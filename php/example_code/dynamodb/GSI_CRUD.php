<?php
// snippet-sourcedescription:[GSI_CRUD.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.GSI_CRUD] 

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

$tableName = 'Issues';

echo "# Creating table $tableName...\n";

try {
    $response = $dynamodb->createTable ( [
        'TableName' => $tableName,
        'AttributeDefinitions' => [
            [ 'AttributeName' => 'IssueId', 'AttributeType' => 'S' ],
            [ 'AttributeName' => 'Title', 'AttributeType' => 'S' ],
            [ 'AttributeName' => 'CreateDate', 'AttributeType' => 'S' ],
            [ 'AttributeName' => 'DueDate', 'AttributeType' => 'S' ] 
        ],
        'KeySchema' => [
            [ 'AttributeName' => 'IssueId', 'KeyType' => 'HASH' ],  //Partition key
            [ 'AttributeName' => 'Title', 'KeyType' => 'RANGE' ]  //Sort key
        ],
        'GlobalSecondaryIndexes' => [
            [
                'IndexName' => 'CreateDateIndex',
                'KeySchema' => [
                    [ 'AttributeName' => 'CreateDate', 'KeyType' => 'HASH' ],  //Partition key
                    [ 'AttributeName' => 'IssueId', 'KeyType' => 'RANGE' ]  //Sort key
                ],
                'Projection' => [
                    'ProjectionType' => 'INCLUDE',
                    'NonKeyAttributes' => [ 'Description', 'Status' ] 
                ],
                'ProvisionedThroughput' => [
                    'ReadCapacityUnits' => 1, 'WriteCapacityUnits' => 1 
                ] 
            ],
            [
                'IndexName' => 'TitleIndex',
                'KeySchema' => [
                    [ 'AttributeName' => 'Title', 'KeyType' => 'HASH' ], //Partition key
                    [ 'AttributeName' => 'IssueId', 'KeyType' => 'RANGE' ]  //Sort key
                ],
                'Projection' => [ 'ProjectionType' => 'KEYS_ONLY' ],
                'ProvisionedThroughput' => [
                    'ReadCapacityUnits' => 1, 'WriteCapacityUnits' => 1 
                ] 
            ],
            [
                'IndexName' => 'DueDateIndex',
                'KeySchema' => [
                    [ 'AttributeName' => 'DueDate', 'KeyType' => 'HASH' ]  //Partition key
                ],
                'Projection' => [
                    'ProjectionType' => 'ALL' 
                ],
                'ProvisionedThroughput' => [
                    'ReadCapacityUnits' => 1, 'WriteCapacityUnits' => 1 ] 
            ] 
        ],
        'ProvisionedThroughput' => [
            'ReadCapacityUnits' => 1, 'WriteCapacityUnits' => 1 ] 
    ]);

    echo "  Waiting for table $tableName to be created.\n";
        $dynamodb->waitUntil('TableExists', [
            'TableName' => $tableName,
            '@waiter' => [
                'delay'       => 5,
                'maxAttempts' => 20
            ]
        ]);
    echo "  Table $tableName has been created.\n";

} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to create table $tableName\n");
}

// ########################################
// Add items to the table

echo "# Loading data into $tableName...\n";

$response = $dynamodb->putItem ([
    'TableName' => $tableName,
    'Item' => [
        'IssueId' => [ 'S' => 'A-101' ],
        'Title' => [ 'S' => 'Compilation error' ],
        'Description' => [
            'S' => 'Can\'t compile Project X - bad version number. What does this mean?' ],
        'CreateDate' => [ 'S' => '2014-11-01' ],
        'LastUpdateDate' => [ 'S' => '2014-11-02' ],
        'DueDate' => [ 'S' => '2014-11-10' ],
        'Priority' => [ 'N' => '1' ],
        'Status' => [ 'S' => 'Assigned' ] 
    ] 
]);

$response = $dynamodb->putItem ([
    'TableName' => $tableName,
    'Item' => [
        'IssueId' => [ 'S' => 'A-102' ],
        'Title' => [ 'S' => 'Can\'t read data file' ],
        'Description' => [
            'S' => 'The main data file is missing, or the permissions are incorrect' ],
        'CreateDate' => [ 'S' => '2014-11-01' ],
        'LastUpdateDate' => [ 'S' => '2014-11-04' ],
        'DueDate' => [ 'S' => '2014-11-30' ],
        'Priority' => [ 'N' => '2' ],
        'Status' => [ 'S' => 'In progress' ] 
    ] 
]);

$response = $dynamodb->putItem ([
    'TableName' => $tableName,
    'Item' => [
        'IssueId' => [ 'S' => 'A-103' ],
        'Title' => [ 'S' => 'Test failure' ],
        'Description' => [
            'S' => 'Functional test of Project X produces errors.' ],
        'CreateDate' => [ 'S' => '2014-11-01' ],
        'LastUpdateDate' => [ 'S' => '2014-11-02' ],
        'DueDate' => [ 'S' => '2014-11-10' ],
        'Priority' => [ 'N' => '1' ],
        'Status' => [ 'S' => 'In progress' ] 
    ] 
]);

$response = $dynamodb->putItem ([
    'TableName' => $tableName,
    'Item' => [
        'IssueId' => [ 'S' => 'A-104' ],
        'Title' => [ 'S' => 'Compilation error' ],
        'Description' => [
            'S' => 'Variable "messageCount" was not initialized.' ],
        'CreateDate' => [ 'S' => '2014-11-15' ],
        'LastUpdateDate' => [ 'S' => '2014-11-16' ],
        'DueDate' => [ 'S' => '2014-11-30' ],
        'Priority' => [ 'N' => '3' ],
        'Status' => [ 'S' => 'Assigned' ] 
    ] 
]);

$response = $dynamodb->putItem ([
    'TableName' => $tableName,
    'Item' => [
        'IssueId' => [ 'S' => 'A-105' ],
        'Title' => [ 'S' => 'Network issue' ],
        'Description' => [
            'S' => 'Can\'t ping IP address 127.0.0.1. Please fix this.' ],
        'CreateDate' => [ 'S' => '2014-11-15' ],
        'LastUpdateDate' => [ 'S' => '2014-11-16' ],
        'DueDate' => [ 'S' => '2014-11-19' ],
        'Priority' => [ 'N' => '5' ],
        'Status' => [ 'S' => 'Assigned' ] 
    ] 
]);

// ########################################
// Query for issues filed on 2014-11-01

$response = $dynamodb->query ( [
    'TableName' => $tableName,
    'IndexName' => 'CreateDateIndex',
    'KeyConditionExpression' => 
        'CreateDate = :v_dt and begins_with(IssueId, :v_issue)',
    'ExpressionAttributeValues' =>  [
        ':v_dt' => ['S' => '2014-11-01'],
        ':v_issue' => ['S' => 'A-']
    ]
]);

echo '# Query for issues filed on 2014-11-01:' . "\n";
foreach ( $response ['Items'] as $item ) {
    echo ' - ' . $item ['CreateDate'] ['S'] . 
        ' ' . $item ['IssueId'] ['S'] . 
        ' ' . $item ['Description'] ['S'] . 
        ' ' . $item ['Status'] ['S'] . 
        "\n";
}

echo "\n";

// ########################################
// Query for issues that are 'Compilation errors'

$response = $dynamodb->query ( [
    'TableName' => $tableName,
    'IndexName' => 'TitleIndex',
    'KeyConditionExpression' => 
        'Title = :v_title and IssueId >= :v_issue',
    'ExpressionAttributeValues' =>  [
        ':v_title' => ['S' => 'Compilation error'],
        ':v_issue' => ['S' => 'A-']
    ]
]);

echo '# Query for issues that are compilation errors: ' . "\n";

foreach ( $response ['Items'] as $item ) {
    echo ' - ' . $item ['Title'] ['S'] . 
    ' ' . $item ['IssueId'] ['S'] . 
    "\n";
}

echo "\n";

// ########################################
// Query for items that are due on 2014-11-30

$response = $dynamodb->query ( [
    'TableName' => $tableName,
    'IndexName' => 'DueDateIndex',
    'KeyConditionExpression' => 'DueDate = :v_dt',
    'ExpressionAttributeValues' =>  [
        ':v_dt' => ['S' => '2014-11-30']
    ]
]);

echo "# Querying for items that are due on 2014-11-30:\n";
foreach ( $response ['Items'] as $item ) {
    
    echo ' - ' . $item ['DueDate'] ['S'] . 
        ' ' . $item ['IssueId'] ['S'] . 
        ' ' . $item ['Title'] ['S'] . 
        ' ' . $item ['Description'] ['S'] . 
        ' ' . $item ['CreateDate'] ['S'] . 
        ' ' . $item ['LastUpdateDate'] ['S'] . 
        ' ' . $item ['Priority'] ['N'] . 
        ' ' . $item ['Status'] ['S'] . "\n";
}

echo "\n";

// ########################################
// Delete the table

try {
    echo "# Deleting table $tableName...\n";
    $dynamodb->deleteTable(['TableName' => $tableName]);
    echo "  Waiting for table $tableName to be deleted.\n";

    $dynamodb->waitUntil('TableNotExists', [
        'TableName' => $tableName,
        '@waiter' => [
            'delay'       => 5,
            'maxAttempts' => 20
        ]
    ]);

    echo "  Table $tableName has been deleted.\n";

} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to delete table $tableName\n");
}



// snippet-end:[dynamodb.php.codeexample.GSI_CRUD] 
?>