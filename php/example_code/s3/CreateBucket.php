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
// snippet-start:[s3.php.create_bucket.complete]
// snippet-start:[s3.php.create_bucket.import]
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.create_bucket.import]
/**
 * Create an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[s3.php.create_bucket.main]
class CreateBucketExample
{
    public function createBucket($s3Client, $bucketName)
    {
        try {
            $result = $s3Client->createBucket([
                'Bucket' => $bucketName,
            ]);
        } catch (AwsException $e) {
            echo $e->getMessage();
            echo "\n";
            return false;
        }

        return true;
    }    
}
// snippet-end:[s3.php.create_bucket.main] 
// snippet-end:[s3.php.create_bucket.complete]

use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;

# Relies on PHPUnit to test the functionality in the preceding code.
class CreateBucketExampleTest extends TestCase
{
    const BUCKET_NAME = 'my-bucket';
    const REGION = 'us-east-1';
    private $s3Client;
    private $mock;
    private $s3ClientMock;
    private $createBucketExample;
    
    protected function setUp(): void
    {
        $this->s3Client = new S3Client([
            'profile' => 'default',
            'region' => self::REGION,
            'version' => '2006-03-01'
        ]);

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $this->s3ClientMock = new S3Client([
            'profile' => 'default',
            'region' => self::REGION,
            'version' => '2006-03-01',
            'handler' => $mock
        ]);

        $this->createBucketExample = new CreateBucketExample();
    }

    # Note: Running this test might result in changes and charges to your AWS account.
    public function testActuallyCreatesABucket()
    {
        $this->assertEquals($this->createBucketExample->createBucket(
            $this->s3Client, self::BUCKET_NAME), true);
    }

    public function testMocksCreatingABucket()
    {
        $this->assertEquals($this->createBucketExample->createBucket(
            $this->s3ClientMock, self::BUCKET_NAME), true);
    }

}
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateBucket.php demonstrates how to create an new Amazon S3 bucket given a name to use for the bucket.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon S3]
// snippet-service:[s3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-09-20]
// snippet-sourceauthor:[pccornel (AWS)]

