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
 *  https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service/s3-presigned-post.html
 *
 */
// snippet-start:[s3.php.presigned_post.complete]
// snippet-start:[s3.php.presigned_post.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;  
use Aws\Exception\AwsException;
// snippet-end:[s3.php.presigned_post.import]


// snippet-start:[s3.php.presigned_post.main]
$client = new S3Client([
    'profile' => 'default',
    'version' => 'latest',
    'region' => 'us-west-2',
]);
$bucket = 'mybucket';

// Set some defaults for form input fields
$formInputs = ['acl' => 'public-read'];

// Construct an array of conditions for policy
$options = [
    ['acl' => 'public-read'],
    ['bucket' => $bucket],
    ['starts-with', '$key', 'user/eric/'],
];

// Optional: configure expiration time string
$expires = '+2 hours';

$postObject = new \Aws\S3\PostObjectV4(
    $client,
    $bucket,
    $formInputs,
    $options,
    $expires
);

// Get attributes to set on an HTML form, e.g., action, method, enctype
$formAttributes = $postObject->getFormAttributes();

// Get form input fields. This will include anything set as a form input in
// the constructor, the provided JSON policy, your AWS access key ID, and an
// auth signature.
$formInputs = $postObject->getFormInputs();
 
 
// snippet-end:[s3.php.presigned_post.main]
// snippet-end:[s3.php.presigned_post.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PresignedPost.php demonstrates how to presign a POST request so that a user can send items to your Amazon S3 bucket without having to sign into your AWS Account. This is often used in connection with a form.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-09-20]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

