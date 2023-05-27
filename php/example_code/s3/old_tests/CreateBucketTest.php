<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./CreateBucket.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite s3-createbucket
*/

namespace S3;

use Aws\MockHandler;
use Aws\Result;
use Aws\S3\S3Client;
use PHPUnit\Framework\TestCase;

class CreateBucketTest extends TestCase
{
    public function testCreatesABucket()
    {
        require(__DIR__ . '/../CreateBucket.php');

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $s3Client = new S3Client([
            'profile' => AWS_ACCOUNT_PROFILE_NAME,
            'region' => AWS_REGION_ID,
            'version' => S3_API_VERSION,
            'handler' => $mock
        ]);

        $this->assertEquals(createBucket(
            $s3Client,
            S3_BUCKET_NAME
        ), 'The bucket\'s location is: ' . '. ' .
            'The bucket\'s effective URI is: ' .
            'https://my-bucket.s3.amazonaws.com/');
    }
}
