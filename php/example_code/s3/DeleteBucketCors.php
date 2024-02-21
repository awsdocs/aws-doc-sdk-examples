<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.php.delete_bucket_cors.complete]
// snippet-start:[s3.php.delete_bucket_cors.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.delete_bucket_cors.import]

/**
 * Delete bucket cors
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

$bucketName = 'BUCKET_NAME';

// snippet-start:[s3.php.delete_bucket_cors.main]
$client = new S3Client([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2006-03-01'
]);

try {
    $result = $client->deleteBucketCors([
        'Bucket' => $bucketName, // REQUIRED
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[s3.php.delete_bucket_cors.main]
// snippet-end:[s3.php.delete_bucket_cors.complete]
