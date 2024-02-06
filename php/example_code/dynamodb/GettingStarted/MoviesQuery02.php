<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.MoviesQuery02]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

use Aws\DynamoDb\Exception\DynamoDbException;
use Aws\DynamoDb\Marshaler;

$sdk = new Aws\Sdk([
    'endpoint'   => 'http://localhost:8000',
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();
$marshaler = new Marshaler();

$tableName = 'Movies';

$eav = $marshaler->marshalJson('
    {
        ":yyyy":1992,
        ":letter1": "A",
        ":letter2": "L"
    }
');

$params = [
    'TableName' => $tableName,
    'ProjectionExpression' => '#yr, title, info.genres, info.actors[0]',
    'KeyConditionExpression' =>
        '#yr = :yyyy and title between :letter1 and :letter2',
    'ExpressionAttributeNames' => [ '#yr' => 'year' ],
    'ExpressionAttributeValues' => $eav
];

echo "Querying for movies from 1992 - titles A-L, with genres and lead actor\n";

try {
    $result = $dynamodb->query($params);

    echo "Query succeeded.\n";

    foreach ($result['Items'] as $i) {
        $movie = $marshaler->unmarshalItem($i);
        print $movie['year'] . ': ' . $movie['title'] . ' ... ';

        foreach ($movie['info']['genres'] as $gen) {
            print $gen . ' ';
        }

        echo ' ... ' . $movie['info']['actors'][0] . "\n";
    }
} catch (DynamoDbException $e) {
    echo "Unable to query:\n";
    echo $e->getMessage() . "\n";
}

// snippet-end:[dynamodb.php.codeexample.MoviesQuery02]
