<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.php.create_bucket.complete]
// snippet-start:[s3.php.create_bucket.import]
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.create_bucket.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Creates a bucket in Amazon S3.
 * 
 * Inputs:
 * - $s3Client: An initialized PHP SDK API client for S3.
 * - $bucketName: The name of the bucket to create.
 * 
 * Returns: Information about the bucket; otherwise the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.php.create_bucket.main]
function createBucket($s3Client, $bucketName)
{
    try {
        $result = $s3Client->createBucket([
            'Bucket' => $bucketName,
        ]);
        return 'The bucket\'s location is: ' .
            $result['Location'] . '. ' .
            'The bucket\'s effective URI is: ' . 
            $result['@metadata']['effectiveUri'];
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function createTheBucket()
{
    $s3Client = new S3Client([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2006-03-01'
    ]);

    echo createBucket($s3Client, 'my-bucket');
}

// Uncomment the following line to run this code in an AWS account.
// createTheBucket();
// snippet-end:[s3.php.create_bucket.main] 
// snippet-end:[s3.php.create_bucket.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateBucket.php demonstrates how to create an new Amazon S3 bucket given a name to use for the bucket.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-14]
// snippet-sourceauthor:[pccornel (AWS)]

