<?php
// snippet-sourcedescription:[LSI_CRUD.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.LSI_CRUD] 

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

$tableName = 'CustomerOrders';
echo "# Creating table $tableName...\n";


try {
    $response = $dynamodb->createTable([
        'TableName' => $tableName,
        'AttributeDefinitions' => [
            [ 'AttributeName' => 'CustomerId', 'AttributeType' => 'S' ],
            [ 'AttributeName' => 'OrderId', 'AttributeType' => 'N' ],
            [ 'AttributeName' => 'OrderCreationDate', 'AttributeType' => 'N' ],
            [ 'AttributeName' => 'IsOpen', 'AttributeType' => 'N' ]
        ],
        'KeySchema' => [
            [ 'AttributeName' => 'CustomerId', 'KeyType' => 'HASH' ],  //Partition key
            [ 'AttributeName' => 'OrderId', 'KeyType' => 'RANGE' ] //Sort key
        ],
        'LocalSecondaryIndexes' => [
            [
                'IndexName' => 'OrderCreationDateIndex',
                'KeySchema' => [
                    [ 'AttributeName' => 'CustomerId', 'KeyType' => 'HASH' ],  //Partition key
                    [ 'AttributeName' => 'OrderCreationDate', 'KeyType' => 'RANGE' ]  //Sort key
                ],
                'Projection' => [
                    'ProjectionType' => 'INCLUDE',
                    'NonKeyAttributes' => ['ProductCategory', 'ProductName']
                ]
            ],
            [
                'IndexName' => 'IsOpenIndex',
                'KeySchema' => [
                    [ 'AttributeName' => 'CustomerId', 'KeyType' => 'HASH' ],  //Partition key
                    [ 'AttributeName' => 'IsOpen', 'KeyType' => 'RANGE' ]  //Sort key
                ],
                'Projection' => [ 'ProjectionType' => 'ALL' ]
            ]
        ],
        'ProvisionedThroughput' => [
             'ReadCapacityUnits' => 5, 'WriteCapacityUnits' => 5
        ]
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

#########################################
# Add items to the table

echo "# Loading data into $tableName...\n";

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'alice@example.com'],
        'OrderId' => ['N' => '1'],
        'IsOpen' => ['N' => '1'],
        'OrderCreationDate' => ['N' => '20140101'],
        'ProductCategory' => ['S' => 'Book'],
        'ProductName' => ['S' => 'The Great Outdoors'],
        'OrderStatus' => ['S' => 'PACKING ITEMS']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'alice@example.com'],
        'OrderId' => ['N' => '2'],
        'IsOpen' => ['N' => '1'],
        'OrderCreationDate' => ['N' => '20140221'],
        'ProductCategory' => ['S' => 'Bike'],
        'ProductName' => ['S' => 'Super Mountain'],
        'OrderStatus' => ['S' => 'ORDER RECEIVED']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'alice@example.com'],
        'OrderId' => ['N' => '3'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140304'],
        'ProductCategory' => ['S' => 'Music'],
        'ProductName' => ['S' => 'A Quiet Interlude'],
        'OrderStatus' => ['S' => 'IN TRANSIT'],
        'ShipmentTrackingId' => ['N' => '176493']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '1'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140111'],
        'ProductCategory' => ['S' => 'Movie'],
        'ProductName' => ['S' => 'Calm Before The Storm'],
        'OrderStatus' => ['S' => 'SHIPPING DELAY'],
        'ShipmentTrackingId' => ['N' => '859323']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '2'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140124'],
        'ProductCategory' => ['S' => 'Music'],
        'ProductName' => ['S' => 'E-Z Listening'],
        'OrderStatus' => ['S' => 'DELIVERED'],
        'ShipmentTrackingId' => ['N' => '756943']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '3'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140221'],
        'ProductCategory' => ['S' => 'Music'],
        'ProductName' => ['S' => 'Symphony 9'],
        'OrderStatus' => ['S' => 'DELIVERED'],
        'ShipmentTrackingId' => ['N' => '645193']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '4'],
        'IsOpen' => ['N' => '1'],
        'OrderCreationDate' => ['N' => '20140222'],
        'ProductCategory' => ['S' => 'Hardware'],
        'ProductName' => ['S' => 'Extra Heavy Hammer'],
        'OrderStatus' => ['S' => 'PACKING ITEMS']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '5'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140309'],
        'ProductCategory' => ['S' => 'Book'],
        'ProductName' => ['S' => 'How To Cook'],
        'OrderStatus' => ['S' => 'IN TRANSIT'],
        'ShipmentTrackingId' => ['N' => '440185']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '6'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140318'],
        'ProductCategory' => ['S' => 'Luggage'],
        'ProductName' => ['S' => 'Really Big Suitcase'],
        'OrderStatus' => ['S' => 'DELIVERED'],
        'ShipmentTrackingId' => ['N' => '893927']
    ]
]);

$response = $dynamodb->putItem ( [
    'TableName' => $tableName,
    'Item' => [
        'CustomerId' => ['S' => 'bob@example.com'],
        'OrderId' => ['N' => '7'],
        // no IsOpen attribute
        'OrderCreationDate' => ['N' => '20140324'],
        'ProductCategory' => ['S' => 'Golf'],
        'ProductName' => ['S' => 'PGA Pro II'],
        'OrderStatus' => ['S' => 'OUT FOR DELIVERY'],
        'ShipmentTrackingId' => ['N' => '383283']
    ] 
]);


#########################################
# Query for Bob's 5 most recent orders in 2014, retrieving attributes which 
# are projected into the index

$response = $dynamodb->query([
    'TableName' => $tableName,
    'IndexName' => 'OrderCreationDateIndex',
    'KeyConditionExpression' => 'CustomerId = :v_id and OrderCreationDate >= :v_dt',
    'ExpressionAttributeValues' =>  [
        ':v_id' => ['S' => 'bob@example.com'],
        ':v_dt' => ['N' => '20140101']
    ],
    'Select' => 'ALL_PROJECTED_ATTRIBUTES',
    'ScanIndexForward' => false,
    'ConsistentRead' => true,
    'Limit' => 5,
    'ReturnConsumedCapacity' => 'TOTAL'
]);

echo "# Querying for Bob's 5 most recent orders in 2014:\n";
foreach($response['Items'] as $item) {
    echo '   - ' . $item['CustomerId']['S'] . 
        ' ' . $item['OrderCreationDate']['N'] . 
        ' ' . $item['ProductName']['S'] . 
        ' ' . $item['ProductCategory']['S'] . 
        "\n";
}
echo ' Provisioned Throughput Consumed: ' . 
    $response['ConsumedCapacity']['CapacityUnits'] . "\n";

#########################################
# Query for Bob's 5 most recent orders in 2014, retrieving some attributes 
# which are not projected into the index

$response = $dynamodb->query([
    'TableName' => $tableName,
    'IndexName' => 'OrderCreationDateIndex',
    'KeyConditionExpression' => 'CustomerId = :v_id and OrderCreationDate >= :v_dt',
    'ExpressionAttributeValues' =>  [
        ':v_id' => ['S' => 'bob@example.com'],
        ':v_dt' => ['N' => '20140101']
    ],
    'Select' => 'SPECIFIC_ATTRIBUTES',
    'ProjectionExpression' => 
        'CustomerId, OrderCreationDate, ProductName, ProductCategory, OrderStatus',
    'ScanIndexForward' => false,
    'ConsistentRead' => true,
    'Limit' => 5,
    'ReturnConsumedCapacity' => 'TOTAL'
]);

echo "# Querying for Bob's 5 most recent orders in 2014:" . "\n";
foreach($response['Items'] as $item) {
    echo '   - ' . $item['CustomerId']['S'] . 
    ' ' . $item['OrderCreationDate']['N'] . 
    ' ' . $item['ProductName']['S'] . 
    ' ' . $item['ProductCategory']['S'] . 
    ' ' . $item['OrderStatus']['S'] . 
    "\n";
}
echo ' Provisioned Throughput Consumed: ' . 
    $response['ConsumedCapacity']['CapacityUnits'] . "\n";

#########################################
# Query for Alice's open orders, fetching all attributes 
# (which are already projected into the index)

$response = $dynamodb->query([
    'TableName' => $tableName,
    'IndexName' => 'IsOpenIndex',
    'KeyConditionExpression' => 'CustomerId = :v_id',
    'ExpressionAttributeValues' =>  [
        ':v_id' => ['S' => 'alice@example.com']
    ],
    'Select' => 'ALL_ATTRIBUTES',
    'ScanIndexForward' => false,
    'ConsistentRead' => true,
    'Limit' => 5,
    'ReturnConsumedCapacity' => 'TOTAL'
]);

echo "# Querying for Alice's open orders:" . "\n";
foreach($response['Items'] as $item) {
    echo '   - ' . $item['CustomerId']['S']. 
    ' ' . $item['OrderCreationDate']['N'] . 
    ' ' . $item['ProductName']['S'] . 
    ' ' . $item['ProductCategory']['S'] . 
    ' ' . $item['OrderStatus']['S'] . 
    "\n";
}

echo ' Provisioned Throughput Consumed: ' . 
    $response['ConsumedCapacity']['CapacityUnits'] . "\n";


#########################################
# Delete the table

try {
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



// snippet-end:[dynamodb.php.codeexample.LSI_CRUD] 
?>