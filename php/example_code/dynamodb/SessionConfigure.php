<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service_dynamodb-session-handler.html
 *
 */
// snippet-start:[dynamodb.php.configure_handler.complete]
// snippet-start:[dynamodb.php.configure_handler.import]

require 'vendor/autoload.php';

use Aws\DynamoDb\SessionHandler;

// snippet-end:[dynamodb.php.configure_handler.import]

/**
 * Lock a DynamoDB Session.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 *  To use the DynamoDB SessionHhandler, your configured credentials must have permission to use the DynamoDB table.
 */

// snippet-start:[dynamodb.php.configure_handler.snippet]
$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions',
    'hash_key' => 'id',
    'session_lifetime' => 3600,
    'consistent_read' => true,
    'locking' => false,
    'batch_config' => [],
    'max_lock_wait_time' => 10,
    'min_lock_retry_microtime' => 5000,
    'max_lock_retry_microtime' => 50000,
]);

// snippet-end:[dynamodb.php.configure_handler.snippet]
// snippet-end:[dynamodb.php.configure_handler.complete]
// snippet-sourceauthor:[AWS]
