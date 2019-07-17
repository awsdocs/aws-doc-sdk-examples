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
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/s3-examples-creating-buckets.html
 *
 */
 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[s3-bucket-website-configuration.php demonstrates how to create, retrieve, and delete a website in an Amazon S3 bucket.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[putBucketWebsite]
// snippet-keyword:[getBucketWebsite]
// snippet-keyword:[deleteBucketWebsite]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
// snippet-sourceauthor:[fletpatr (AWS)]
// snippet-start:[s3.php.example.bucketwebsiteconfiguration]
require 'vendor/autoload.php';

use Aws\S3\S3Client;

$bucket = '*** Your Bucket Name ***';
                
$s3 = new S3Client([
    'version' => 'latest',
    'region'  => 'us-east-1'
]);

         
// Add the website configuration.
$s3->putBucketWebsite([
    'Bucket'                => $bucket,
    'WebsiteConfiguration'  => [
        'IndexDocument' => ['Suffix' => 'index.html'],
        'ErrorDocument' => ['Key' => 'error.html']
    ]
]);
        
// Retrieve the website configuration.
$result = $s3->getBucketWebsite([
    'Bucket' => $bucket
]);
echo $result->getPath('IndexDocument/Suffix');
        
// Delete the website configuration.
$s3->deleteBucketWebsite([
    'Bucket' => $bucket
]);
// snippet-end:[s3.php.example.bucketwebsiteconfiguration]