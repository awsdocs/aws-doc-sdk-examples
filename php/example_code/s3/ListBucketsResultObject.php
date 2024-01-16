<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started/basic-usage.html
 *
 */

// snippet-start:[s3.php.list_bucket_results.complete]
// snippet-start:[s3.php.list_bucket_results.import]
require 'vendor/autoload.php';
use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.list_bucket_results.import]

/**
 * Working with Result objects.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.list_bucket_results.main]
// snippet-start:[s3.php.list_bucket_results.client]
// Use the us-east-2 region and latest version of each client.
$sharedConfig = [
    'profile' => 'default',
    'region' => 'us-east-2',
];

// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk($sharedConfig);

// Use an Aws\Sdk class to create the S3Client object.
// snippet-start:[s3.php.list_bucket_results.call]
$s3 = $sdk->createS3();
$result = $s3->listBuckets();
// snippet-end:[s3.php.list_bucket_results.call]
foreach ($result['Buckets'] as $bucket) {
    echo $bucket['Name'] . "\n";
}

// Convert the result object to a PHP array
$array = $result->toArray();
// snippet-end:[s3.php.list_bucket_results.client]
// Get the name of each bucket
// snippet-start:[s3.php.list_bucket_results.result]
$names = $result->search('Buckets[].Name');
// snippet-end:[s3.php.list_bucket_results.result]
// snippet-end:[s3.php.list_bucket_results.main]
// snippet-end:[s3.php.list_bucket_results.complete]
