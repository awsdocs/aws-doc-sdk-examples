<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_commands.html
 *
 */
// snippet-start:[s3.php.mock_handler.complete]
// snippet-start:[s3.php.mock_handler.import]
use Aws\MockHandler;
use Aws\Result;

// snippet-end:[s3.php.mock_handler.import]
/**
 * Create a MockHandler to list all the files in an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.mock_handler.main]
// Create a mock handler
$mock = new MockHandler();
// Enqueue a mock result to the handler
$mock->append(new Result(['foo' => 'bar']));
// Create a "ListObjects" command
$command = $s3Client->getCommand('ListObjects');
// Associate the mock handler with the command
$command->getHandlerList()->setHandler($mock);
// Executing the command will use the mock handler, which will return the
// mocked result object
$result = $client->execute($command);

echo $result['foo']; // Outputs 'bar'

// snippet-end:[s3.php.mock_handler.main]
// snippet-end:[s3.php.mock_handler.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
