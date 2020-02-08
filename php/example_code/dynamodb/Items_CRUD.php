<?php
// snippet-sourcedescription:[Items_CRUD.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Items_CRUD] 

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

// ###################################################################
// Adding data to the table


echo "# Adding data to table $tableName...\n";

$response = $dynamodb->putItem([
    'TableName' => $tableName,
    'Item' => [
        'Id' => ['N' => '120'],
        'Title' => ['S' => 'Book 120 Title'],
        'ISBN' => ['S' => '120-1111111111'],
        'Authors' => ['SS' => ['Author12', 'Author22']],
        'Price' => ['N' => '20'],
        'Category' => ['S' => 'Book'],
        'Dimensions' => ['S' => '8.5x11.0x.75'],
        'InPublication' => ['BOOL' => false],
    ],
    'ReturnConsumedCapacity' => 'TOTAL'
]);

echo "Consumed capacity: " . $response ["ConsumedCapacity"] ["CapacityUnits"] . "\n";

$response = $dynamodb->putItem([
    'TableName' => $tableName,
    'Item' => [
        'Id' => ['N' => '121'],
        'Title' => ['S' => 'Book 121 Title'],
        'ISBN' => ['S' => '121-1111111111'],
        'Authors' => ['SS' => ['Author21', 'Author22']],
        'Price' => ['N' => '20'],
        'Category' => ['S' => 'Book'],
        'Dimensions' => ['S' => '8.5x11.0x.75'],
        'InPublication' => ['BOOL' => true],
    ],
    'ReturnConsumedCapacity' => 'TOTAL'
]);

echo "Consumed capacity: " . $response ["ConsumedCapacity"] ["CapacityUnits"] . "\n";

// ###################################################################
// Getting an item from the table

echo "\n\n";
echo "# Getting an item from table $tableName...\n";

$response = $dynamodb->getItem ([
    'TableName' => $tableName,
    'ConsistentRead' => true,
    'Key' => [
        'Id' => [
            'N' => '120' 
        ] 
    ],
    'ProjectionExpression' => 'Id, ISBN, Title, Authors' 
] );
print_r ( $response ['Item'] );

// ###################################################################
// Updating item attributes

echo "\n\n";
echo "# Updating an item and returning the whole new item in table $tableName...\n";

$response = $dynamodb->updateItem ( [
    'TableName' => $tableName,
    'Key' => [
        'Id' => [
            'N' => '120' //was 121 
        ] 
    ],
    'ExpressionAttributeNames' => [
        '#NA' => 'NewAttribute',
        '#A' => 'Authors'
    ],
    'ExpressionAttributeValues' =>  [
        ':val1' => ['S' => 'Some Value'],
        ':val2' => ['SS' => ['Author YY','Author ZZ']]
    ] ,
    'UpdateExpression' => 'set #NA = :val1, #A = :val2',
    'ReturnValues' => 'ALL_NEW' 
]);
print_r ( $response ['Attributes'] );

// ###################################################################
// Conditionally updating the Price attribute, only if it has not changed.

echo "\n\n";
echo "# Updating an item attribute only if it has not changed in table $tableName...\n";

$response = $dynamodb->updateItem ( [
    'TableName' => $tableName,
    'Key' => [
        'Id' => [
            'N' => '121' 
        ] 
    ],
    'ExpressionAttributeNames' => [
        '#P' => 'Price'
    ],
    'ExpressionAttributeValues' => [
        ':val1' => ['N' => '25'],
        ':val2' => ['N' => '20'],
    ],
    'UpdateExpression' => 'set #P = :val1',
    'ConditionExpression' => '#P = :val2',
    'ReturnValues' => 'ALL_NEW' 
]);

print_r ( $response ['Attributes'] );

// ###################################################################
// Deleting an item

echo "\n\n";
echo "# Deleting an item and returning its previous values from in table $tableName...\n";

$response = $dynamodb->deleteItem ( [
    'TableName' => $tableName,
    'Key' => [
        'Id' => [
            'N' => '121' 
        ] 
    ],
    'ReturnValues' => 'ALL_OLD'
]);
print_r ( $response ['Attributes']);



// snippet-end:[dynamodb.php.codeexample.Items_CRUD] 
?>