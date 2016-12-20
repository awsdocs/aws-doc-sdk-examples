<?php
/**
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 */

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;

/**
 * Delete an Object inside Amazon S3.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$USAGE = "\n" .
    "To run this example, supply the name of an S3 bucket and object\n" .
    "name (key) to delete.\n" .
    "\n" .
    "Ex: php DeleteObject.php <bucketname> <objectname>\n";

if (count($argv) <= 2){
    echo $USAGE;
    exit();
}

$bucket = $argv[1];
$key = $argv[2];

try{
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
