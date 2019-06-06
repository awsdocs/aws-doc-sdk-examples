<?php
// snippet-sourcedescription:[Scan_ParallelScan.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Scan_ParallelScan] 

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
use Aws\CommandPool;

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableName = 'ProductCatalog';
$totalSegments = 5;
$params = array(
    'TableName' => $tableName,
    'ExpressionAttributeValues' =>  array (
        ':val1' => array('S' => 'Book')
    ) ,
    'FilterExpression' => 'contains (Title, :val1)',
    'Limit' => 10,
    'TotalSegments' => $totalSegments
);

// Build an array of Scan commands - one for each segment
$commands = [];
for ($segment = 0; $segment < $totalSegments; $segment++) {
    $params['Segment'] = $segment;
    $commands[] = $dynamodb->getCommand('Scan',$params);
}

// Setup a command pool to run the Scan commands concurrently
// The 'fulfilled' callback will process the results from each command 
// The 'rejected' callback will tell why the command failed
$pool = new CommandPool($dynamodb,$commands,[
   'fulfilled' => function($result, $iterKey, $aggregatePromise) {
        echo "\nResults from segment $iterKey\n";

        // Do something with the items
        foreach ($result['Items'] as $item) {
            echo "Scanned item with Title \"" . $item['Title']['S'] . "\"\n";
        }
    },
    'rejected' => function ($reason, $iterKey, $aggregatePromise) {
        echo "Failed {$iterKey}: {$reason}\n";
    }
]);

$promise = $pool->promise();
$promise->wait();



// snippet-end:[dynamodb.php.codeexample.Scan_ParallelScan] 
?>