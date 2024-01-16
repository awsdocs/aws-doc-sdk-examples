<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*/
// snippet-start:[s3.php.delete_bucket.complete]
// snippet-start:[s3.php.delete_bucket.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;  
use Aws\Exception\AwsException;
// snippet-end:[s3.php.delete_bucket.import]


/**
 * Delete an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 *
 * ++ Warning ++ This code will actually delete the bucket that you specify, as
 *               well as any objects within it!
 */

$BUCKET_NAME = '<BUCKET-NAME>';

//Create a S3Client 
// snippet-start:[s3.php.delete_bucket.main]
$s3Client = new S3Client([
    'region' => 'us-west-2',
    'version' => '2006-03-01'
]);

//Delete all Objects when versioning is not enabled
try {
    $objects = $s3Client->getIterator('ListObjects', ([
        'Bucket' => $BUCKET_NAME
    ]));
    echo "Keys retrieved!\n";
    foreach ($objects as $object) {
        echo $object['Key'] . "\n";
        $result = $s3Client->deleteObject([
            'Bucket' => $BUCKET_NAME,
            'Key' => $object['Key'],
        ]);
    }
    $result = $s3Client->deleteBucket([
        'Bucket' => $BUCKET_NAME,
    ]);
} catch (S3Exception $e) {
    echo $e->getMessage() . "\n";
}

//Delete bucket and all versioned objects inside bucket when versioning is enabled.
try {
    $versions = $s3Client->listObjectVersions([
        'Bucket' => $BUCKET_NAME
    ])->getPath('Versions');
    echo "Keys retrieved!\n";
    foreach ($versions as $version) {
        echo $version['Key'] . "\n";
        echo $version['VersionId'] . "\n";
        $result = $s3Client->deleteObject([
            'Bucket' => $bucket,
            'Key' => $version['Key'],
            'VersionId' => $version['VersionId']
        ]);
    }
    $result = $s3Client->deleteBucket([
        'Bucket' => $BUCKET_NAME,
    ]);
} catch (S3Exception $e) {
    echo $e->getMessage() . "\n";
}
 
 
// snippet-end:[s3.php.delete_bucket.main]
// snippet-end:[s3.php.delete_bucket.complete]

