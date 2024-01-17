<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * This file shows how to use the CommandPool class provided with the AWS SDK for PHP.
 * It uses the S3Client for this example, but the CommandPool class can be used with
 * many SDK clients. See https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_commands.html
 * for more information.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 * It also assumes it will be run in a *nix environment.
 */

namespace ClassExamples;

# snippet-start:[php.class_examples.command_pool.complete]
# snippet-start:[php.class_examples.command_pool.import]
include __DIR__ . "/vendor/autoload.php";

use Aws\CommandInterface;
use Aws\CommandPool;
use Aws\Exception\AwsException;
use Aws\ResultInterface;
use Aws\S3\S3Client;
use DirectoryIterator;
use GuzzleHttp\Promise\PromiseInterface;
use Iterator;
use S3\S3Service;

# snippet-end:[php.class_examples.command_pool.import]

# snippet-start:[php.class_examples.command_pool.main]
$client = new S3Client([]);

$s3Service = new S3Service($client, true);

$bucket = 'my-bucket-' . uniqid(); // This bucket will be deleted at the end of this example.

$client->createBucket([
    "Bucket" => $bucket,
]);

// Create an iterator that yields files from a directory
$files = new DirectoryIterator(__DIR__);

// Create a generator that converts the SplFileInfo objects into
// Aws\CommandInterface objects. This generator accepts the iterator that
// yields files and the name of the bucket to upload the files to.
$commandGenerator = function (Iterator $files, $bucket) use ($client) {
    /** @var DirectoryIterator $file */
    foreach ($files as $file) {
        // Skip "." and ".." files as well as directories
        if ($file->isDot() || $file->isDir()) {
            continue;
        }
        $filename = $file->getPath() . '/' . $file->getFilename();
        // Yield a command that will be executed by the pool
        yield $client->getCommand('PutObject', [
            'Bucket' => $bucket,
            'Key'    => $file->getBaseName(),
            'Body'   => fopen($filename, 'r')
        ]);
    }
};

// Now create the generator using the files iterator
$commands = $commandGenerator($files, $bucket);

// Create a pool and provide an optional configuration array
$pool = new CommandPool($client, $commands, [
    // Only send 5 files at a time (this is set to 25 by default)
    'concurrency' => 5,
    // Invoke this function before executing each command
    'before' => function (CommandInterface $cmd, $iterKey) {
        echo "About to send {$iterKey}: "
            . print_r($cmd->toArray(), true) . "\n";
    },
    // Invoke this function for each successful transfer
    'fulfilled' => function (
        ResultInterface $result,
        $iterKey,
        PromiseInterface $aggregatePromise
    ) {
        echo "Completed {$iterKey}: {$result}\n";
    },
    // Invoke this function for each failed transfer
    'rejected' => function (
        AwsException $reason,
        $iterKey,
        PromiseInterface $aggregatePromise
    ) {
        echo "Failed {$iterKey}: {$reason}\n";
    },
]);

// Initiate the pool transfers
$promise = $pool->promise();

// Force the pool to complete synchronously
$promise->wait();

// Or you can chain the calls off of the pool
$promise->then(function () {
    echo "Done\n";
});

//Clean up the created bucket
$s3Service->emptyAndDeleteBucket($bucket);
# snippet-end:[php.class_examples.command_pool.main]
# snippet-end:[php.class_examples.command_pool.complete]
