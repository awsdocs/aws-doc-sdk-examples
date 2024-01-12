<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*/
// snippet-start:[s3.php.delete_object.complete]
// snippet-start:[s3.php.delete_object.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;  
use Aws\Exception\AwsException;
// snippet-end:[s3.php.delete_object.import]


/**
 * Delete an Object inside Amazon S3.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

 // snippet-start:[s3.php.delete_object.main]
$USAGE = "\n" .
    "To run this example, supply the name of an S3 bucket and object\n" .
    "name (key) to delete.\n" .
    "\n" .
    "Ex: php DeleteObject.php <bucketname> <objectname>\n";

if (count($argv) <= 2) {
    echo $USAGE;
    exit();
}

$bucket = $argv[1];
$key = $argv[2];

try {
    //Create a S3Client
    $s3Client = new S3Client([
        'region' => 'us-west-2',
        'version' => '2006-03-01'
    ]);
    $result = $s3Client->deleteObject([
        'Bucket' => $bucket,
        'Key' => $key,
    ]);
} catch (S3Exception $e) {
    echo $e->getMessage() . "\n";
}
 
 
// snippet-end:[s3.php.delete_object.main]
// snippet-end:[s3.php.delete_object.complete]

