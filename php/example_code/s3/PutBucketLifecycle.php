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
 */
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;

/**
 * Put bucket lifecycle
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$bucketName = 'BUCKET_NAME';

$client = new S3Client([
    'region' => 'us-west-2',
    'version' => '2006-03-01'
]);

try {
    $result = $client->putBucketLifecycle([
            'Bucket' => $bucketName, // REQUIRED
            'LifecycleConfiguration' => [
                'Rules' => [ // REQUIRED
                    [
                        'AbortIncompleteMultipartUpload' => [
                            'DaysAfterInitiation' => 30,
                        ],
                        'ID' => 'unique_id',
                        'Status' => 'Enabled', // REQUIRED
                        'Prefix' => ''
                    ],
                ],
            ],
        ]
    );
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
