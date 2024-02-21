<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service_dynamodb-session-handler.html *
 *
 */
// snippet-start:[dynamodb.php.start_session.complete]
// snippet-start:[dynamodb.php.start_session.register_handler]

require 'vendor/autoload.php';

use Aws\DynamoDb\SessionHandler;

$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions'
]);

$sessionHandler->register();
// snippet-end:[dynamodb.php.start_session.register_handler]

/**
 * SessionStart.php shows how to register a DynamoDB Session Handler.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 *  To use the DynamoDB SessionHhandler, your configured credentials must have permission to use the DynamoDB table.
 */

// snippet-start:[dynamodb.php.start_session.snippet]
// Start the session
session_start();

// Alter the session data
$_SESSION['user.name'] = 'jeremy';
$_SESSION['user.role'] = 'admin';

// Close the session (optional, but recommended)
session_write_close();
// snippet-end:[dynamodb.php.start_session.snippet]
// snippet-end:[dynamodb.php.start_session.complete]
// snippet-sourceauthor:[AWS]
