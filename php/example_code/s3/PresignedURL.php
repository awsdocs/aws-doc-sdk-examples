<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 *  https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service/s3-presigned-url.html
 *
 */
// snippet-start:[s3.php.presigned_url.complete]
// snippet-start:[s3.php.presigned_url.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;  
use Aws\Exception\AwsException;
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
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PresignedURL.php demonstrates how to create a presigned url for an object in an Amazon S3 Bucket so you can give it to a user without them needing to authenticate to your AWS account.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-09-20]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

