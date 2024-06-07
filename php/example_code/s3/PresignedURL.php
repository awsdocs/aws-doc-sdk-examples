<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.php.presigned_url.complete]
namespace S3;
// snippet-start:[s3.php.presigned_url.import]
use Aws\Exception\AwsException;
use AwsUtilities\PrintableLineBreak;
use AwsUtilities\TestableReadline;
use DateTime;

require 'vendor/autoload.php';
// snippet-end:[s3.php.presigned_url.import]

class PresignedURL
{
    use PrintableLineBreak;
    use TestableReadline;

    public function run()
    {
        // snippet-start:[s3.php.presigned_url.main]
        $s3Service = new S3Service();

        $expiration = new DateTime("+20 minutes");
        $linebreak = $this->getLineBreak();

        echo $linebreak;
        echo ("Welcome to the Amazon S3 presigned URL demo.\n");
        echo $linebreak;

        $bucket = $this->testable_readline("First, please enter the name of the S3 bucket to use: ");
        $key = $this->testable_readline("Next, provide the key of an object in the given bucket: ");
        echo $linebreak;
        // snippet-start:[s3.php.presigned_url.get_object]
        $command = $s3Service->getClient()->getCommand('GetObject', [
            'Bucket' => $bucket,
            'Key' => $key,
        ]);
        // snippet-end:[s3.php.presigned_url.get_object]
        // snippet-start:[s3.php.presigned_url.create_url]
        try {
            // snippet-start:[s3.php.presigned_url.get_url
            $preSignedUrl = $s3Service->preSignedUrl($command, $expiration);
            // snippet-end:[s3.php.presigned_url.get_url
            echo "Your preSignedUrl is \n$preSignedUrl\nand will be good for the next 20 minutes.\n";
            echo $linebreak;
            echo "Thanks for trying the Amazon S3 presigned URL demo.\n";
        } catch (AwsException $exception) {
            echo $linebreak;
            echo "Something went wrong: $exception";
            die();
        }
        // snippet-end:[s3.php.presigned_url.create_url]
        // snippet-end:[s3.php.presigned_url.main]
    }
}

$runner = new PresignedURL();
$runner->run();

// snippet-end:[s3.php.presigned_url.complete]
