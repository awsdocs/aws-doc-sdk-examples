<?php
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
 *
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/sqs-examples-enable-long-polling.html
 *
 */
// snippet-start:[sqs.php.long_polling_create_queue.complete]
// snippet-start:[sqs.php.long_polling_create_queue.import]
require 'vendor/autoload.php';

use Aws\Sqs\SqsClient; 
use Aws\Exception\AwsException;
// snippet-end:[sqs.php.long_polling_create_queue.import]

/**
 * Create SQS Queue with Long Polling
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[sqs.php.long_polling_create_queue.main]
$queueName = "QUEUE_NAME";
 

$client = new SqsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2012-11-05'
]);

try {
    $result = $client->createQueue(array(
        'QueueName' => $queueName,
        'Attributes' => array(
            'ReceiveMessageWaitTimeSeconds' => 20
        ),
    ));
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
 
 
// snippet-end:[sqs.php.long_polling_create_queue.main]
// snippet-end:[sqs.php.long_polling_create_queue.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LongPollingCreateQueue.php demonstrates how to create a queue with a default 20 second to wait between retrieving messages. This will reduce the number of empty responses returned and thus your bill.]
#snippet-keyword:[Python]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Simple Queue Service]
// snippet-service:[sqs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-27]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

