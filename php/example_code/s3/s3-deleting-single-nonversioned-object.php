<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/s3-examples-creating-buckets.html
 *
 */
 
// snippet-start:[s3.php.example.deletingsinglenonversionedobject]
require 'vendor/autoload.php';

use Aws\S3\S3Client;

$bucket = '*** Your Bucket Name ***';
$keyname = '*** Your Object Key ***';

$s3 = new S3Client([
    'version' => 'latest',
    'region'  => 'us-east-1'
]);

// Delete an object from the bucket.
$s3->deleteObject([
    'Bucket' => $bucket,
    'Key'    => $keyname
]);
// snippet-end:[s3.php.example.deletingsinglenonversionedobject]