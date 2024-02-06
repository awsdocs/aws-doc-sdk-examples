<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the AWS SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/kinesis-example-shard.html
 *
 */
// snippet-start:[kinesis.php.list_data_stream_shards.complete]
// snippet-start:[kinesis.php.list_data_stream_shards.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[kinesis.php.list_data_stream_shards.import]

/**
 * List existing shards for current Amazon Kinesis Data Stream.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KinesisClient
// snippet-start:[kinesis.php.list_data_stream_shards.main]
$kinesisClient = new Aws\Kinesis\KinesisClient([
    'profile' => 'default',
    'version' => '2013-12-02',
    'region' => 'us-east-2'
]);

$name = "my_stream_name";

try {
    $result = $kinesisClient->ListShards([
        'StreamName' => $name,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[kinesis.php.list_data_stream_shards.main]
// snippet-end:[kinesis.php.list_data_stream_shards.complete]
