<?php
// snippet-sourcedescription:[SampleDataQuery.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.SampleDataQuery] 

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

$fourteenDaysAgo = date('Y-m-d H:i:s', strtotime('-14 days'));

$response = $dynamodb->query([
    'TableName' => 'Reply',

    'KeyConditionExpression' => 'Id = :v_id and ReplyDateTime >= :v_reply_dt',
    'ExpressionAttributeValues' =>  [
        ':v_id' => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'],
        ':v_reply_dt' => ['S' => $fourteenDaysAgo]
    ]
]);

print_r ($response['Items']);



// snippet-end:[dynamodb.php.codeexample.SampleDataQuery] 
?>