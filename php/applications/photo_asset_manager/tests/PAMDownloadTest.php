<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager\Tests;

use Aws\CommandInterface;
use Aws\DynamoDb\DynamoDbClient;
use Aws\S3\S3Client;
use phpmock\phpunit\PHPMock;
use PHPUnit\Framework\TestCase;
use Psr\Http\Message\RequestInterface;
use ZipArchive;

use function PhotoAssetManager\createZipArchive;
use function PhotoAssetManager\createAPresignedUrlToDownloadTheZip;
use function PhotoAssetManager\formatLabels;
use function PhotoAssetManager\getImages;

/**
 * @group unit
 */
class PAMDownloadTest extends TestCase
{
    use PHPMock;

    public static function setUpBeforeClass(): void
    {
        include __DIR__ . "/../src/download.php";
    }

    public function testCreateZipArchive()
    {
        $s3Client = $this->createMock(S3Client::class);
        $s3Client->expects($this->once())
                 ->method('registerStreamWrapper');
        $tableName = "testTableName";
        $storageBucket = "testStorageBucketName";
        $workingBucket = "testWorkingBucketName";
        $responses = [$tableName => [0 => ['Images' => ['L' => [0 => ['testAttribute' => 'testValue']]]]]];
        $fgcMock = $this->getFunctionMock('PhotoAssetManager', 'file_get_contents');
        $fgcMock->expects($this->once())->willReturn("fileString");
        $zipArchiver = $this->createMock(ZipArchive::class);
        $zipArchiver->expects($this->once())->method('open');
        $zipArchiver->expects($this->once())->method('addFromString');
        $zipArchiver->expects($this->once())->method('close');

        createZipArchive($s3Client, $responses, $storageBucket, $workingBucket, $tableName, $zipArchiver, 1);
    }

    public function testCreateAPresignedUrlToDownloadTheZip()
    {
        $s3Client = $this->createMock(S3Client::class);
        $request = $this->createMock(RequestInterface::class);
        $s3Client->expects($this->once())->method("createPresignedRequest")->willReturn($request);
        $command = $this->createMock(CommandInterface::class);
        $s3Client->expects($this->once())->method("getCommand")->willReturn($command);
        $bucket = "testBucket";
        $zipName = "testZipName";

        createAPresignedUrlToDownloadTheZip($s3Client, $bucket, $zipName);
    }

    public function testGetImages()
    {
        $DDBClient = $this->createMock(DynamoDbClient::class);
        $tableName = "testTableName";
        $labels = ['labelA', 'labelB'];

        getImages($DDBClient, $tableName, $labels);
        $this->assertTrue(true);
    }

    public function testFormatLabels()
    {
        $labels = ['labelA', 'labelB'];

        $formattedLabels = formatLabels($labels);

        $targetFormat = [
            0 => ["Label" => ["S" => 'labelA']],
            1 => ["Label" => ["S" => 'labelB']]
        ];

        $this->assertEquals($targetFormat, $formattedLabels);
    }
}
