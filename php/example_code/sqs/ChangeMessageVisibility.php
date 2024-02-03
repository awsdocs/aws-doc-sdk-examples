<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sqs.php.change_message_visibility.complete]
// snippet-start:[sqs.php.change_message_visibility.import]
require 'vendor/autoload.php';

use Aws\Sqs\SqsClient;
use Aws\Exception\AwsException;
// snippet-end:[sqs.php.change_message_visibility.import]

/**
 * Changes the visibility timeout of a specified message in a queue to a new value
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[sqs.php.change_message_visibility.main]

$queueUrl = "QUEUE_URL";

$client = new SqsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2012-11-05'
]);

try {
    $result = $client->receiveMessage(array(
        'AttributeNames' => ['SentTimestamp'],
        'MaxNumberOfMessages' => 1,
        'MessageAttributeNames' => ['All'],
        'QueueUrl' => $queueUrl, // REQUIRED
    ));
    if ($result->get('Messages') != null) {
        $result = $client->changeMessageVisibility([
            'QueueUrl' => $queueUrl, // REQUIRED
            'ReceiptHandle' => $result->get('Messages')[0]['ReceiptHandle'], // REQUIRED
            'VisibilityTimeout' => 3600, // REQUIRED
        ]);
        var_dump($result);
    } else {
        echo "No messages in queue \n";
    }
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[sqs.php.change_message_visibility.main]
// snippet-end:[sqs.php.change_message_visibility.complete]
