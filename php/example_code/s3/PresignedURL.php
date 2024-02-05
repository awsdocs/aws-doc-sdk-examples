<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 *  https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service/s3-presigned-url.html
 *
 */
// snippet-start:[s3.php.presigned_url.complete]
// snippet-start:[s3.php.presigned_url.import]

require 'vendor/autoload.php';

// snippet-end:[s3.php.presigned_url.import]

//Creating a presigned request
// snippet-start:[s3.php.presigned_url.main]
// snippet-start:[s3.php.presigned_url.get_object]
$s3Client = new Aws\S3\S3Client([
    'profile' => 'default',
    'region' => 'us-east-2',
    'version' => '2006-03-01',
]);

$cmd = $s3Client->getCommand('GetObject', [
    'Bucket' => 'my-bucket',
    'Key' => 'testKey'
]);

$request = $s3Client->createPresignedRequest($cmd, '+20 minutes');
// snippet-end:[s3.php.presigned_url.get_object]

// snippet-start:[s3.php.presigned_url.create_url]
//Creating a presigned URL
$cmd = $s3Client->getCommand('GetObject', [
    'Bucket' => 'my-bucket',
    'Key' => 'testKey'
]);

$request = $s3Client->createPresignedRequest($cmd, '+20 minutes');

// Get the actual presigned-url
$presignedUrl = (string)$request->getUri();
// snippet-end:[s3.php.presigned_url.create_url]

// snippet-start:[s3.php.presigned_url.get_url]
//Getting the URL to an object
$url = $s3Client->getObjectUrl('my-bucket', 'my-key');
// snippet-end:[s3.php.presigned_url.get_url]

// snippet-end:[s3.php.presigned_url.main]
// snippet-end:[s3.php.presigned_url.complete]
