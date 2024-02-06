<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.php.codeexample.Query]
require 'vendor/autoload.php';

date_default_timezone_set('UTC');

$sdk = new Aws\Sdk([
    'region'   => 'us-west-2',
    'version'  => 'latest'
]);

$dynamodb = $sdk->createDynamoDb();

$fourteenDaysAgo = date('Y-m-d H:i:s', strtotime('-14 days'));
$tableName = 'Reply';

# The Query API is paginated. Issue the Query request multiple times.
do {
    echo "Querying table $tableName\n";

    $request = [
        'TableName' => $tableName,
        'KeyConditionExpression' => 'Id = :v_id and ReplyDateTime >= :v_reply_dt',
        'ExpressionAttributeValues' =>  [
            ':v_id' => ['S' => 'Amazon DynamoDB#DynamoDB Thread 2'],
            ':v_reply_dt' => ['S' => $fourteenDaysAgo]
        ],
        'ProjectionExpression' => 'Id, ReplyDateTime, Message, PostedBy',
        'ConsistentRead' => true,
        'Limit' => 1
    ];

    # Add the ExclusiveStartKey if we got one back in the previous response
    if (isset($response) && isset($response['LastEvaluatedKey'])) {
        $request['ExclusiveStartKey'] = $response['LastEvaluatedKey'];
    }

    $response = $dynamodb->query($request);

    foreach ($response['Items'] as $key => $value) {
        echo 'Id: ' . $value['Id']['S'] . "\n";
        echo 'ReplyDateTime: ' . $value['ReplyDateTime']['S'] . "\n";
        echo 'Message: ' . $value['Message']['S'] . "\n";
        echo 'PostedBy: ' . $value['PostedBy']['S'] . "\n";
        echo "\n";
    }

# If there is no LastEvaluatedKey in the response, then
# there are no more items matching this Query
} while (isset($response['LastEvaluatedKey']));

// snippet-end:[dynamodb.php.codeexample.Query]
