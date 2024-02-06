<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service_dynamodb-session-handler.html
 *
 */
// snippet-start:[dynamodb.php.garbage_collection.complete]
// snippet-start:[dynamodb.php.garbage_collection.register_handler]

require 'vendor/autoload.php';

use Aws\DynamoDb\SessionHandler;

$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions'
]);

$sessionHandler->register();
// snippet-end:[dynamodb.php.garbage_collection.register_handler]

/**
 * Automate the Garbage Collection of a DynamoDB Session.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 *  To use the DynamoDB SessionHhandler, your configured credentials must have permission to use the DynamoDB table.
 */

// snippet-start:[dynamodb.php.garbage_collection.snippet]
$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions',
    'batch_config' => [
        'before' => function ($command) {
            $command['@http']['delay'] = 5000;
        }
    ]
]);

$sessionHandler->garbageCollect();

// snippet-end:[dynamodb.php.garbage_collection.snippet]
// snippet-end:[dynamodb.php.garbage_collection.complete]
// snippet-sourceauthor:[AWS]
