<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager\Tests;

use Aws\CommandInterface;
use Aws\S3\S3Client;
use phpmock\phpunit\PHPMock;
use PHPUnit\Framework\TestCase;
use Psr\Http\Message\RequestInterface;

use function PhotoAssetManager\getPresignedRequest;

/**
 * @group unit
 */
class PAMUploadTest extends TestCase
{
    use PHPMock;

    public static function setUpBeforeClass(): void
    {
        include __DIR__ . "/../src/upload.php";
    }

    public function testGetPresignedRequest()
    {
        $s3Client = $this->createMock(S3Client::class);
        $request = $this->createMock(RequestInterface::class);
        $s3Client->expects($this->once())->method("createPresignedRequest")->willReturn($request);
        $command = $this->createMock(CommandInterface::class);
        $s3Client->expects($this->once())->method("getCommand")->willReturn($command);
        $bucket = "testBucket";
        $fileName = "testFileName";
        getPresignedRequest($s3Client, $bucket, $fileName);
    }
}
