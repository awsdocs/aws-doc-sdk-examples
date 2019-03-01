<?php
// snippet-sourcedescription:[Tables_CRUD.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Tables_CRUD] 

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
                'KeyType' => 'HASH'  //Partition key
            ]
        ],
        'ProvisionedThroughput' => [
            'ReadCapacityUnits'    => 5,
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
    
    print_r($response->getPath('TableDescription'));
    
    echo "table $tableName has been created.\n";
} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to create table $tableName\n");
}

####################################################################
# Updating the table

echo "# Updating the provisioned throughput of table $tableName.\n"; 
try {

$response = $dynamodb->updateTable([
    'TableName' => $tableName,
    'ProvisionedThroughput'    => [
        'ReadCapacityUnits'    => 6,
        'WriteCapacityUnits' => 7
    ]
]);

$dynamodb->waitUntil('TableExists', [
    'TableName' => $tableName,
    '@waiter' => [
        'delay'       => 5,
        'maxAttempts' => 20
    ]
]); 

echo "New provisioned throughput settings:\n";

$response = $dynamodb->describeTable(['TableName' => $tableName]);

echo "Read capacity units: " . 
    $response['Table']['ProvisionedThroughput']['ReadCapacityUnits']."\n";
echo "Write capacity units: " . 
    $response['Table']['ProvisionedThroughput']['WriteCapacityUnits']."\n";

} catch (DynamoDbException $e) {
    echo $e->getMessage() . "\n";
    exit ("Unable to update table $tableName\n");
}

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
    exit ("Unable to delete table $tableName\n");
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
    }else {
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
?>