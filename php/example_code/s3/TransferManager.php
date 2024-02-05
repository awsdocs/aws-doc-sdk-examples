<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 *  https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service/s3-transfer.html
 *
 */
// snippet-start:[s3.php.transfer_manager.complete]
// snippet-start:[s3.php.transfer_manager.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\S3\Transfer;

// snippet-end:[s3.php.transfer_manager.import]

// Create an S3 client
// snippet-start:[s3.php.transfer_manager.main]
$client = new S3Client([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2006-03-01',
]);

//Uploading a Local Director to S3
echo "Uploading Files to S3";

// Where the files will be sourced from
$source = '/path/to/source/files';

// Where the files will be transferred to
$dest = 's3://bucket/foo';

// Create a default transfer object
$manager = new Transfer($client, $source, $dest);

// Perform the transfer synchronously
$manager->transfer();

//Downloading an S3 Bucket
echo "Downloading Files form S3";

//Switch the Source and destination to download from S3
$source = 's3://bucket';
$dest = '/path/to/destination/dir';

// Create a default transfer object
$manager = new Transfer($client, $source, $dest);

//toggle to transfer asynchronously
$async = true;
if ($async) {
    // Initiate the transfer and get a promise
    $promise = $manager->promise();

    // Do something when the transfer is complete using the then() method
    $promise->then(function () {
        echo 'Done!';
    });
} else {
    // Perform the transfer synchronously
    $manager->transfer();
}

// snippet-end:[s3.php.transfer_manager.main]
// snippet-end:[s3.php.transfer_manager.complete]
