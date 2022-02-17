<?php
// snippet-sourcedescription:[Scan_SerialScan.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Scan_SerialScan] 

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
$params = [
    'TableName' => $tableName,
    'ExpressionAttributeValues' => [
        ':val1' => ['S' => 'Book']
    ],
    'FilterExpression' => 'contains (Title, :val1)',
    'Limit' => 10 
];

// Execute scan operations until the entire table is scanned
$count = 0;
do {
    $response = $dynamodb->scan ( $params );
    $items = $response->get ( 'Items' );
    $count = $count + count ( $items );
    
    // Do something with the $items
    foreach ( $items as $item ) {
        echo "Scanned item with Title \"{$item['Title']['S']}\".\n";
    }
    
    // Set parameters for next scan
    $params ['ExclusiveStartKey'] = $response ['LastEvaluatedKey'];
} while ( $params ['ExclusiveStartKey'] );

echo "{$tableName} table scanned completely. {$count} items found.\n";



// snippet-end:[dynamodb.php.codeexample.Scan_SerialScan] 
?>