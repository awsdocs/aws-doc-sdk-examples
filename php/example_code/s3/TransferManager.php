<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 *  https://docs.aws.amazon.com/aws-sdk-php/v3/guide/service/s3-transfer.html
 *
 */

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;

// Create an S3 client
$client = new \Aws\S3\S3Client([
    'region'  => 'us-west-2',
    'version' => '2006-03-01',
]);

//Uploading a Local Director to S3
echo "Uploading Files to S3";

// Where the files will be source from
$source = '/path/to/source/files';

// Where the files will be transferred to
$dest = 's3://bucket/foo';

// Create a default transfer object
$manager = new \Aws\S3\Transfer($client, $source, $dest);

// Perform the transfer synchronously
$manager->transfer();

//Downloading an S3 Bucket
echo "Downloading Files form S3";

//Switch the Source and destination to download from S3
$source = 's3://bucket';
$dest = '/path/to/destination/dir';

// Create a default transfer object
$manager = new \Aws\S3\Transfer($client, $source, $dest);

//toggle to transfer asynchronously 
if (async) {
    // Initiate the transfer and get a promise
    $promise = $manager->promise();

    // Do something when the transfer is complete using the then() method
    $promise->then(function () {
        echo 'Done!';
    });
}
else {
    // Perform the transfer synchronously
    $manager->transfer();

}