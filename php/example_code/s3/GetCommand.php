<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/s3-examples-creating-buckets.html
 *
 */
// snippet-start:[s3.php.command_pool_get_command.complete]
// snippet-start:[s3.php.command_pool_get_command.import]
use Aws\CommandPool;
use Aws\S3\S3Client;

// snippet-end:[s3.php.command_pool_get_command.import]
/**
 * Use Command Pool to upload a file to an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// Create the client
// snippet-start:[s3.php.command_pool_get_command.main]
$client = new S3Client([
    'region'  => 'us-standard',
    'version' => '2006-03-01'
]);

$bucket = 'example';
$commands = [
    $client->getCommand('HeadObject', ['Bucket' => $bucket, 'Key' => 'a']),
    $client->getCommand('HeadObject', ['Bucket' => $bucket, 'Key' => 'b']),
    $client->getCommand('HeadObject', ['Bucket' => $bucket, 'Key' => 'c'])
];

$pool = new CommandPool($client, $commands);

// Initiate the pool transfers
$promise = $pool->promise();

// Force the pool to complete synchronously
$promise->wait();

// snippet-end:[s3.php.command_pool_get_command.main]
// snippet-end:[s3.php.command_pool_get_command.complete]
