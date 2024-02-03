<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Items_BatchWrite]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$tableNameOne = 'Forum';
$tableNameTwo = 'Thread';

$response = $dynamodb->batchWriteItem([
    'RequestItems' => [
        $tableNameOne => [
            [
                'PutRequest' => [
                    'Item' => [
                        'Name'   => ['S' => 'Amazon S3 Forum'],
                        'Threads' => ['N' => '0']
                    ]]
            ]
        ],
         $tableNameTwo => [
            [
                'PutRequest' => [
                    'Item' => [
                        'ForumName'   => ['S' => 'Amazon S3 Forum'],
                        'Subject' => ['S' => 'My sample question'],
                        'Message' => ['S' => 'Message Text.'],
                        'KeywordTags' => ['SS' => ['Amazon S3', 'Bucket']]
                    ]]
            ],
            [
                'DeleteRequest' => [
                    'Key' => [
                        'ForumName' => ['S' => 'Some partition key value'],
                        'Subject' => ['S' => 'Some sort key value']
                    ]]
                ]
         ]
    ]
]);

print_r($response);

// snippet-end:[dynamodb.php.codeexample.Items_BatchWrite]
