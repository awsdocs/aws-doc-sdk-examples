<?php
// snippet-sourcedescription:[Query.php demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[PHP]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.php.codeexample.Query] 

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
    if(isset($response) && isset($response['LastEvaluatedKey'])) {
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
} while(isset($response['LastEvaluatedKey'])); 



// snippet-end:[dynamodb.php.codeexample.Query] 
?>