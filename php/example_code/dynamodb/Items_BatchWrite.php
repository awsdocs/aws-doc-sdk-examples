<?php
// snippet-sourcedescription:[Items_BatchWrite.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Items_BatchWrite] 

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
                        'Message'=> ['S' => 'Message Text.'],
                        'KeywordTags'=>['SS' => ['Amazon S3', 'Bucket']]
                    ]]
            ],
            [
                'DeleteRequest' => [
                    'Key' => [
                        'ForumName' =>['S' => 'Some partition key value'],
                        'Subject' => ['S' => 'Some sort key value']
                    ]]
                ]
         ]
    ]
]);

print_r($response);



// snippet-end:[dynamodb.php.codeexample.Items_BatchWrite] 
?>