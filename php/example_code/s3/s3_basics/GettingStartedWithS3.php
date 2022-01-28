<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 *
 * Shows how to use AWS SDK for PHP v3 to get started using Amazon Simple Storage
 * Service (Amazon S3). Create a bucket, move objects into and out of it, and delete all
 * resources at the end of the demo.
 *
 * This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
 * user guide.
 * - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * You will need to run the following command to install dependencies:
 * composer install
 *
 * Then run the example either directly with:
 * php GettingStartedWithS3.php
 *
 * or as a PHPUnit test:
 * vendor/bin/phpunit S3BasicsTests.php
 *
 * /**/

# snippet-start:[php.example_code.s3.Scenario_GettingStarted]
require 'vendor/autoload.php';

use Aws\S3\S3Client;

echo("--------------------------------------\n");
print("Welcome to the Amazon S3 getting started demo using PHP!\n");
echo("--------------------------------------\n");

$region = 'us-west-2';
$version = 'latest';

$s3client = new S3Client([
    'region' => $region,
    'version' => $version
]);
/* Inline declaration example
# snippet-start:[php.example_code.s3.basics.createClient]
$s3client = new Aws\S3\S3Client(['region' => 'us-west-2', 'version' => 'latest']);
# snippet-end:[php.example_code.s3.basics.createClient]
*/

# snippet-start:[php.example_code.s3.basics.bucketName]
$bucket_name = "doc-example-bucket-" . uniqid();
# snippet-end:[php.example_code.s3.basics.bucketName]

# snippet-start:[php.example_code.s3.basics.createBucket]
try {
    $s3client->createBucket([
        'Bucket' => $bucket_name,
        'CreateBucketConfiguration' => ['LocationConstraint' => $region],
    ]);
    echo "Created bucket named: $bucket_name \n";
} catch (Exception $exception) {
    echo "Failed to create bucket $bucket_name with error: " . $exception->getMessage();
    exit("Please fix error with bucket creation before continuing.");
}
# snippet-end:[php.example_code.s3.basics.createBucket]

# snippet-start:[php.example_code.s3.basics.putObject]
$file_name = "local-file-" . uniqid();
try {
    $s3client->putObject([
        'Bucket' => $bucket_name,
        'Key' => $file_name,
        'SourceFile' => 'testfile.txt'
    ]);
    echo "Uploaded $file_name to $bucket_name.\n";
} catch (Exception $exception) {
    echo "Failed to upload $file_name with error: " . $exception->getMessage();
    exit("Please fix error with file upload before continuing.");
}
# snippet-end:[php.example_code.s3.basics.putObject]

# snippet-start:[php.example_code.s3.basics.getObject]
try {
    $file = $s3client->getObject([
        'Bucket' => $bucket_name,
        'Key' => $file_name,
    ]);
    $body = $file->get('Body');
    $body->rewind();
    echo "Downloaded the file and it begins with: {$body->read(26)}.\n";
} catch (Exception $exception) {
    echo "Failed to download $file_name from $bucket_name with error: " . $exception->getMessage();
    exit("Please fix error with file downloading before continuing.");
}
# snippet-end:[php.example_code.s3.basics.getObject]

# snippet-start:[php.example_code.s3.basics.copyObject]
try {
    $folder = "copied-folder";
    $s3client->copyObject([
        'Bucket' => $bucket_name,
        'CopySource' => "$bucket_name/$file_name",
        'Key' => "$folder/$file_name-copy",
    ]);
    echo "Copied $file_name to $folder/$file_name-copy.\n";
} catch (Exception $exception) {
    echo "Failed to copy $file_name with error: " . $exception->getMessage();
    exit("Please fix error with object copying before continuing.");
}
# snippet-end:[php.example_code.s3.basics.copyObject]

# snippet-start:[php.example_code.s3.basics.listObjects]
try {
    $contents = $s3client->listObjects([
        'Bucket' => $bucket_name,
    ]);
    echo "The contents of your bucket are: \n";
    foreach ($contents['Contents'] as $content) {
        echo $content['Key'] . "\n";
    }
} catch (Exception $exception) {
    echo "Failed to list objects in $bucket_name with error: " . $exception->getMessage();
    exit("Please fix error with listing objects before continuing.");
}
# snippet-end:[php.example_code.s3.basics.listObjects]

# snippet-start:[php.example_code.s3.basics.deleteObjects]
try {
    $objects = [];
    foreach ($contents['Contents'] as $content) {
        $objects[] = [
            'Key' => $content['Key'],
        ];
    }
    $s3client->deleteObjects([
        'Bucket' => $bucket_name,
        'Key' => $file_name,
        'Delete' => [
            'Objects' => $objects,
        ],
    ]);
    $check = $s3client->listObjects([
        'Bucket' => $bucket_name,
    ]);
    if (count($check) <= 0) {
        throw new Exception("Bucket wasn't empty.");
    }
    echo "Deleted all objects and folders from $bucket_name.\n";
} catch (Exception $exception) {
    echo "Failed to delete $file_name from $bucket_name with error: " . $exception->getMessage();
    exit("Please fix error with object deletion before continuing.");
}
# snippet-end:[php.example_code.s3.basics.deleteObjects]

# snippet-start:[php.example_code.s3.basics.deleteBucket]
try {
    $s3client->deleteBucket([
        'Bucket' => $bucket_name,
    ]);
    echo "Deleted bucket $bucket_name.\n";
} catch (Exception $exception) {
    echo "Failed to delete $bucket_name with error: " . $exception->getMessage();
    exit("Please fix error with bucket deletion before continuing.");
}
# snippet-end:[php.example_code.s3.basics.deleteBucket]

echo "Successfully ran the Amazon S3 with PHP demo.\n";

# snippet-end:[php.example_code.s3.Scenario_GettingStarted]
