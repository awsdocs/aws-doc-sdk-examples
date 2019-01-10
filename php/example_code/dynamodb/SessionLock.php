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
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service_dynamodb-session-handler.html
 *
 */
// snippet-start:[dynamodb.php.session_lock.complete]
// snippet-start:[dynamodb.php.session_lock.register_handler]

require 'vendor/autoload.php';

use Aws\DynamoDb\SessionHandler;

$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions'
]);

$sessionHandler->register();
// snippet-end:[dynamodb.php.session_lock.register_handler]


/**
 * Lock a DynamoDB Session.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 *  To use the DynamoDB SessionHhandler, your configured credentials must have permission to use the DynamoDB table.
 */


// snippet-start:[dynamodb.php.session_lock.snippet]
$sessionHandler = SessionHandler::fromClient($dynamoDb, [
    'table_name' => 'sessions',
    'locking' => true,
]);
// snippet-end:[dynamodb.php.session_lock.snippet]
// snippet-end:[dynamodb.php.session_lock.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SessionLock.php shows how to use pessimistic session locking to lock a DynamoDB session.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[session_lock]
// snippet-keyword:[Amazon DynamoDB]
// snippet-service:[dynamodb]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]