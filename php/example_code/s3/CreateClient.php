<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.php.create_client.complete]
// snippet-start:[s3.php.create_client.import]
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;

// snippet-end:[s3.php.create_client.import]
/**
 * Creating an Amazon S3 client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.create_client.main]
// snippet-start:[s3.php.create_client.client]
//Create an S3Client
$s3 = new Aws\S3\S3Client([
    'version' => 'latest',
    'region' => 'us-east-2'
]);
// snippet-end:[s3.php.create_client.client]

// snippet-start:[s3.php.create_client.sdk]
// The same options that can be provided to a specific client constructor can also be supplied to the Aws\Sdk class.
// Use the us-west-2 region and latest version of each client.
$sharedConfig = [
    'region' => 'us-west-2',
    'version' => 'latest'
];
// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk($sharedConfig);
// Create an Amazon S3 client using the shared configuration data.
$client = $sdk->createS3();
// snippet-end:[s3.php.create_client.sdk]
// snippet-end:[s3.php.create_client.main]
// snippet-end:[s3.php.create_client.complete]
