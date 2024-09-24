<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 *
 * Shows how to use AWS SDK for PHP v3 to get started using Amazon Simple Storage
 * Service (Amazon S3). Create a bucket, move objects into and out of it, and delete all
 * resources at the end of the demo.
 *
 * This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
 * user guide.
 * - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * You will need to run the following command to install dependencies:
 * composer install
 *
 * Then run the example either directly with:
 * php GettingStartedWithS3.php
 *
 * or as a PHPUnit test:
 * vendor/bin/phpunit S3BasicsTests.php
 * /**/

namespace S3;

use Aws\S3\Exception\S3Exception;
use Aws\S3\S3Client;
use AwsUtilities\RunnableExample;
use Exception;

class GettingStartedWithS3 implements RunnableExample
{
    protected string $bucketName;
    protected S3Client $s3client;

    public function runExample()
    {
        // snippet-start:[php.example_code.s3.Scenario_GettingStarted]
        echo("\n");
        echo("--------------------------------------\n");
        print("Welcome to the Amazon S3 getting started demo using PHP!\n");
        echo("--------------------------------------\n");

        $region = 'us-west-2';

        $this->s3client = new S3Client([
                'region' => $region,
        ]);
        /* Inline declaration example
        # snippet-start:[php.example_code.s3.basics.createClient]
        $s3client = new Aws\S3\S3Client(['region' => 'us-west-2']);
        # snippet-end:[php.example_code.s3.basics.createClient]
        */

        // snippet-start:[php.example_code.s3.basics.bucketName]
        $this->bucketName = "amzn-s3-demo-bucket-" . uniqid();
        // snippet-end:[php.example_code.s3.basics.bucketName]

        // snippet-start:[php.example_code.s3.basics.createBucket]
        try {
            $this->s3client->createBucket([
                'Bucket' => $this->bucketName,
                'CreateBucketConfiguration' => ['LocationConstraint' => $region],
            ]);
            echo "Created bucket named: $this->bucketName \n";
        } catch (Exception $exception) {
            echo "Failed to create bucket $this->bucketName with error: " . $exception->getMessage();
            exit("Please fix error with bucket creation before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.createBucket]

        // snippet-start:[php.example_code.s3.basics.putObject]
        $fileName = __DIR__ . "/local-file-" . uniqid();
        try {
            $this->s3client->putObject([
                'Bucket' => $this->bucketName,
                'Key' => $fileName,
                'SourceFile' => __DIR__ . '/testfile.txt'
            ]);
            echo "Uploaded $fileName to $this->bucketName.\n";
        } catch (Exception $exception) {
            echo "Failed to upload $fileName with error: " . $exception->getMessage();
            exit("Please fix error with file upload before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.putObject]

        // snippet-start:[php.example_code.s3.basics.getObject]
        try {
            $file = $this->s3client->getObject([
                'Bucket' => $this->bucketName,
                'Key' => $fileName,
            ]);
            $body = $file->get('Body');
            $body->rewind();
            echo "Downloaded the file and it begins with: {$body->read(26)}.\n";
        } catch (Exception $exception) {
            echo "Failed to download $fileName from $this->bucketName with error: " . $exception->getMessage();
            exit("Please fix error with file downloading before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.getObject]

        // snippet-start:[php.example_code.s3.basics.copyObject]
        try {
            $folder = "copied-folder";
            $this->s3client->copyObject([
                'Bucket' => $this->bucketName,
                'CopySource' => "$this->bucketName/$fileName",
                'Key' => "$folder/$fileName-copy",
            ]);
            echo "Copied $fileName to $folder/$fileName-copy.\n";
        } catch (Exception $exception) {
            echo "Failed to copy $fileName with error: " . $exception->getMessage();
            exit("Please fix error with object copying before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.copyObject]

        // snippet-start:[php.example_code.s3.basics.listObjects]
        try {
            $contents = $this->s3client->listObjectsV2([
                'Bucket' => $this->bucketName,
            ]);
            echo "The contents of your bucket are: \n";
            foreach ($contents['Contents'] as $content) {
                echo $content['Key'] . "\n";
            }
        } catch (Exception $exception) {
            echo "Failed to list objects in $this->bucketName with error: " . $exception->getMessage();
            exit("Please fix error with listing objects before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.listObjects]

        // snippet-start:[php.example_code.s3.basics.deleteObjects]
        try {
            $objects = [];
            foreach ($contents['Contents'] as $content) {
                $objects[] = [
                    'Key' => $content['Key'],
                ];
            }
            $this->s3client->deleteObjects([
                'Bucket' => $this->bucketName,
                'Delete' => [
                    'Objects' => $objects,
                ],
            ]);
            $check = $this->s3client->listObjectsV2([
                'Bucket' => $this->bucketName,
            ]);
            if (count($check) <= 0) {
                throw new Exception("Bucket wasn't empty.");
            }
            echo "Deleted all objects and folders from $this->bucketName.\n";
        } catch (Exception $exception) {
            echo "Failed to delete $fileName from $this->bucketName with error: " . $exception->getMessage();
            exit("Please fix error with object deletion before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.deleteObjects]

        // snippet-start:[php.example_code.s3.basics.deleteBucket]
        try {
            $this->s3client->deleteBucket([
                'Bucket' => $this->bucketName,
            ]);
            echo "Deleted bucket $this->bucketName.\n";
        } catch (Exception $exception) {
            echo "Failed to delete $this->bucketName with error: " . $exception->getMessage();
            exit("Please fix error with bucket deletion before continuing.");
        }
        // snippet-end:[php.example_code.s3.basics.deleteBucket]

        echo "Successfully ran the Amazon S3 with PHP demo.\n";

        // snippet-end:[php.example_code.s3.Scenario_GettingStarted]
    }

    public function helloService()
    {
        include_once __DIR__ . "/helloS3.php";
    }

    public function cleanUp()
    {
        $s3service = new S3Service($this->s3client);
        try {
            $s3service->emptyAndDeleteBucket($this->bucketName);
        } catch (S3Exception $e) {
            if ($e->getAwsErrorCode() != 'NoSuchBucket') {
                throw $e;
            }
        }
    }
}
