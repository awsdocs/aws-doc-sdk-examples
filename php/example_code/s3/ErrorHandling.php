<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started/basic-usage.html
 *
 */

// snippet-start:[s3.php.error_handling.complete]
// snippet-start:[s3.php.error_handling.import]
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
use Aws\S3\Exception\S3Exception;

// snippet-end:[s3.php.error_handling.import]
/**
 * Create an Amazon S3 bucket. Synchronous Error Handling
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.error_handling.main]
// snippet-start:[s3.php.error_handling.client]
// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk([
    'region'   => 'us-west-2'
]);

// Use an Aws\Sdk class to create the S3Client object.
$s3Client = $sdk->createS3();

try {
    $s3Client->createBucket(['Bucket' => 'my-bucket']);
} catch (S3Exception $e) {
    // Catch an S3 specific exception.
    echo $e->getMessage();
} catch (AwsException $e) {
    // This catches the more generic AwsException. You can grab information
    // from the exception using methods of the exception object.
    echo $e->getAwsRequestId() . "\n";
    echo $e->getAwsErrorType() . "\n";
    echo $e->getAwsErrorCode() . "\n";

    // This dumps any modeled response data, if supported by the service
    // Specific members can be accessed directly (e.g. $e['MemberName'])
    var_dump($e->toArray());
}

// snippet-end:[s3.php.error_handling.client]
// snippet-start:[s3.php.error_handling.async]
//Asynchronous Error Handling
// snippet-start:[s3.php.error_handling.promise]
$promise = $s3Client->createBucketAsync(['Bucket' => 'my-bucket']);
// snippet-end:[s3.php.error_handling.promise]
$promise->otherwise(function ($reason) {
    var_dump($reason);
});

// This does the same thing as the "otherwise" function.
$promise->then(null, function ($reason) {
    var_dump($reason);
});

// snippet-end:[s3.php.error_handling.async]

// snippet-start:[s3.php.error_handling.trycatch]
//throw exception
try {
    $result = $promise->wait();
} catch (S3Exception $e) {
    echo $e->getMessage();
}
// snippet-end:[s3.php.error_handling.trycatch]
// snippet-end:[s3.php.error_handling.main]
// snippet-end:[s3.php.error_handling.complete]
