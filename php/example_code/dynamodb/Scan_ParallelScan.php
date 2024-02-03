<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Scan_ParallelScan]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\CommandPool;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableName = 'ProductCatalog';
$totalSegments = 5;
$params = [
    'TableName' => $tableName,
    'ExpressionAttributeValues' =>  [
        ':val1' => ['S' => 'Book']
    ] ,
    'FilterExpression' => 'contains (Title, :val1)',
    'Limit' => 10,
    'TotalSegments' => $totalSegments
];

// Build an array of Scan commands - one for each segment
$commands = [];
for ($segment = 0; $segment < $totalSegments; $segment++) {
    $params['Segment'] = $segment;
    $commands[] = $dynamodb->getCommand('Scan', $params);
}

// Set up a command pool to run the Scan commands concurrently
// The 'fulfilled' callback will process the results from each command
// The 'rejected' callback will tell why the command failed
$pool = new CommandPool($dynamodb, $commands, [
    'fulfilled' => function ($result, $iterKey) {
        echo "\nResults from segment $iterKey\n";

        // Do something with the items
        foreach ($result['Items'] as $item) {
            echo "Scanned item with Title \"" . $item['Title']['S'] . "\"\n";
        }
    },
    'rejected' => function ($reason, $iterKey) {
        echo "Failed {$iterKey}: {$reason}\n";
    }
]);

$promise = $pool->promise();
$promise->wait();

// snippet-end:[dynamodb.php.codeexample.Scan_ParallelScan]
