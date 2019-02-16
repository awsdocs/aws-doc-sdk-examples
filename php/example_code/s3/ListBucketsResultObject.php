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
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/getting-started/basic-usage.html
 *
 */
// snippet-start:[s3.php.create_client.complete]
// snippet-start:[s3.php.create_client.import]
require 'vendor/autoload.php';
use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.create_client.import]

/**
 * Working with Result objects.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.create_client.main]
// snippet-start:[s3.php.create_client.client]
// Use the us-east-2 region and latest version of each client.
$sharedConfig = [
    'profile' => 'default',
    'region' => 'us-east-2',
    'version' => 'latest'
];

// Create an SDK class used to share configuration across clients.
$sdk = new Aws\Sdk($sharedConfig);

// Use an Aws\Sdk class to create the S3Client object.
// snippet-start:[s3.php.create_client.call]
$s3 = $sdk->createS3();
$result = $s3->listBuckets();
// snippet-end:[s3.php.create_client.call]
foreach ($result['Buckets'] as $bucket) {
    echo $bucket['Name'] . "\n";
}

// Convert the result object to a PHP array
$array = $result->toArray();
// snippet-end:[s3.php.create_client.client]
// Get the name of each bucket
// snippet-start:[s3.php.create_client.result]
$names = $result->search('Buckets[].Name');
// snippet-end:[s3.php.create_client.result]

// snippet-end:[s3.php.create_client.main]
// snippet-end:[s3.php.create_client.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListBucketsResultObject.php demonstrates how to work with a result object by transforming it into an array.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-09-20]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

