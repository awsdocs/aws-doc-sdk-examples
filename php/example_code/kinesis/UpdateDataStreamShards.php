<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the AWS SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/kinesis-example-shard.html
 *
 */
// snippet-start:[kinesis.php.update_data_stream_shards.complete]
// snippet-start:[kinesis.php.update_data_stream_shards.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[kinesis.php.update_data_stream_shards.import]

/**
 * Updating number of shards in an Amazon Kinesis Data Stream.
 * Remember you can only increase shards to be double current shard count.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KinesisClient
// snippet-start:[kinesis.php.update_data_stream_shards.main]
$kinesisClient = new Aws\Kinesis\KinesisClient([
    'profile' => 'default',
    'version' => '2013-12-02',
    'region' => 'us-east-2'
]);

$name = "my_stream_name";
$totalshards = 4;

try {
    $result = $kinesisClient->UpdateShardCount([
        'ScalingType' => 'UNIFORM_SCALING',
        'StreamName' => $name,
        'TargetShardCount' => $totalshards
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[kinesis.php.update_data_stream_shards.main]
// snippet-end:[kinesis.php.update_data_stream_shards.complete]
