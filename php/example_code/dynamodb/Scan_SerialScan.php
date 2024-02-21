<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Scan_SerialScan]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

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
    $response = $dynamodb->scan($params);
    $items = $response->get('Items');
    $count = $count + count($items);

    // Do something with the $items
    foreach ($items as $item) {
        echo "Scanned item with Title \"{$item['Title']['S']}\".\n";
    }

    // Set parameters for next scan
    $params ['ExclusiveStartKey'] = $response ['LastEvaluatedKey'];
} while ($params ['ExclusiveStartKey']);

echo "$tableName table scanned completely. $count items found.\n";

// snippet-end:[dynamodb.php.codeexample.Scan_SerialScan]
