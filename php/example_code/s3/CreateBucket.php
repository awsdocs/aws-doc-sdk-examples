<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ////////////////////////////////////////////////////////////////////////////

Purpose:
  Creates a bucket in Amazon S3.

Prerequisites:
  - You must have an AWS account. For more information, see "How do I create
    and activate a new Amazon Web Services account" on the AWS Premium Support
    website.
  - This code uses default AWS access credentials. For more information, see
    "Credentials for the AWS SDK for PHP" in the AWS SDK for PHP Developer 
    Guide.

Running the code:
  To run this code, use PHPUnit along with the phpunit.xml file in this folder.
  For example:

  ./vendor/bin/phpunit --testsuite s3

Additional information:
  - As an AWS best practice, grant this code least privilege, or only the 
    permissions required to perform a task. For more information, see 
    "Grant Least Privilege" in the AWS Identity and Access Management 
    User Guide.
  - This code has not been tested in all AWS Regions. Some AWS services are 
    available only in specific Regions. For more information, see the 
    "AWS Regional Table" on the AWS website.
  - Running this code outside of the included PHPUnit test might result in 
    charges to your AWS account.

//////////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.php.create_bucket.complete]
// snippet-start:[s3.php.create_bucket.import]
require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;

// snippet-end:[s3.php.create_bucket.import]
// snippet-start:[s3.php.create_bucket.main]
class CreateBucketExample
{
    private $s3Client = null;

    public function __construct($s3Client)
    {
        $this->s3Client = $s3Client;
    }

    /* ////////////////////////////////////////////////////////////////////////

    Purpose: Creates a bucket in Amazon S3.

    Inputs:
      - $bucketName: The name of the bucket to create.

    Returns: true if the API call succeeds; otherwise, false.

    //////////////////////////////////////////////////////////////////////// */
    public function createBucket($bucketName)
    {
        try {
            $result = $this->s3Client->createBucket([
                'Bucket' => $bucketName,
            ]);
            // var_dump($result);
        } catch (AwsException $e) {
            echo $e->getMessage();
            echo "\n";
            return false;
        }

        if ($result['@metadata']['statusCode'] == 200) {
            return true;
        } else {
            return false;
        }
    }    
}
// snippet-end:[s3.php.create_bucket.main] 
// snippet-end:[s3.php.create_bucket.complete]

use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;

# Relies on PHPUnit to test the functionality in the preceding code.
# Related custom constants are defined in the phpunit.xml file in this folder.
class CreateBucketExampleTest extends TestCase
{
    public function testCreatesABucket()
    {
        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $this->s3ClientMock = new S3Client([
            'profile' => 'default',
            'region' => AWS_REGION,
            'version' => '2006-03-01',
            'handler' => $mock
        ]);

        $this->createBucketExample = new CreateBucketExample(
            $this->s3ClientMock);

        $this->assertEquals($this->createBucketExample->createBucket(
            S3_BUCKET_NAME), true);
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
// snippet-sourcedate:[2020-03-30]
// snippet-sourceauthor:[pccornel (AWS)]

