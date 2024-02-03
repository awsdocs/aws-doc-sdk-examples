<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.SampleDataQuery]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$fourteenDaysAgo = date('Y-m-d H:i:s', strtotime('-14 days'));

$response = $dynamodb->query([
    'TableName' => 'Reply',

    'KeyConditionExpression' => 'Id = :v_id and ReplyDateTime >= :v_reply_dt',
    'ExpressionAttributeValues' =>  [
        ':v_id' => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'],
        ':v_reply_dt' => ['S' => $fourteenDaysAgo]
    ]
]);

print_r($response['Items']);

// snippet-end:[dynamodb.php.codeexample.SampleDataQuery]
