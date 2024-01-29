<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started/basic-usage.html
 *
 */

// snippet-start:[s3.php.list_buckets_async.complete]
// snippet-start:[s3.php.list_buckets_async.import]
require 'vendor/autoload.php';
use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.list_buckets_async.import]
/**
 * List your Amazon S3 buckets. Asynchronous Requests
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.list_buckets_async.main]
// snippet-start:[s3.php.list_buckets_async.client]
// snippet-start:[s3.php.list_buckets_async.async]
// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk([
    'region'   => 'us-west-2'
]);
// Use an Aws\Sdk class to create the S3Client object.
$s3Client = $sdk->createS3();
// snippet-end:[s3.php.list_buckets_async.client]
//Listing all S3 Bucket
$CompleteSynchronously = $s3Client->listBucketsAsync();
// Block until the result is ready.
$CompleteSynchronously = $CompleteSynchronously->wait();
// snippet-end:[s3.php.list_buckets_async.async]
// snippet-start:[s3.php.list_buckets_async.promise]
$promise = $s3Client->listBucketsAsync();
$promise
    ->then(function ($result) {
        echo 'Got a result: ' . var_export($result, true);
    })
    ->otherwise(function ($reason) {
        echo 'Encountered an error: ' . $reason->getMessage();
    });
// snippet-end:[s3.php.list_buckets_async.promise]
// snippet-end:[s3.php.list_buckets_async.main]
// snippet-end:[s3.php.list_buckets_async.complete]
