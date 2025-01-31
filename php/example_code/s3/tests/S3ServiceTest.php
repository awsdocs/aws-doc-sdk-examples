<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3\tests;

use Aws\AwsClient;
use Aws\CommandInterface;
use Aws\Exception\AwsException;
use Aws\Result;
use Aws\ResultPaginator;
use Aws\S3\S3Client;
use AwsUtilities\AWSServiceClass;
use AwsUtilities\MockS3Client;
use PHPUnit\Framework\TestCase;
use S3\S3Service;

/**
 * @group  unit
 * @covers \S3\S3Service
 */
class S3ServiceTest extends TestCase
{
    protected string $uuid;
    protected AwsClient $client;
    protected AWSServiceClass $service;

    protected Result $startResult;
    protected Result $continueResult;
    protected Result $endResult;

    /**
     * @coversNothing
     */
    protected function setUp(): void
    {
        $this->uuid = uniqid();
        $this->client = $this->createMock(S3Client::class);
        $this->service = new S3Service($this->client);
        $this->service->setVerbose(true);

        $this->startResult = $this->createStub(Result::class);
        $methodMapStart = [
            ['IsTruncated', true],
            ['Content', [1]],
        ];
        $this->startResult->method('offsetGet')->willReturnMap($methodMapStart);

        $this->continueResult = $this->createStub(Result::class);
        $methodMapContinue = [
            ['IsTruncated', true],
            ['Content', [2]],
        ];
        $this->continueResult->method('offsetGet')->willReturnMap($methodMapContinue);

        $this->endResult = $this->createMock(Result::class);
        $methodMapEnd = [
            ['IsTruncated', false],
            ['Content', [3]],
        ];
        $this->endResult->method('offsetGet')->willReturnMap($methodMapEnd);
    }

    /**
     * @covers \S3\S3Service::__construct
     */
    public function testConstructor()
    {
        self::assertInstanceOf(S3Service::class, $this->service);
        $defaultService = new S3Service();
        self::assertInstanceOf(S3Service::class, $defaultService);
    }

    public function testDeleteObjects()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('deleteObjects')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception), true)
        );
        $this->assertNull($this->service->deleteObjects('testBucket', ['testObject1', '$testObject2']));
        $expectedString =
            "Deleted the list of objects from: testBucket.\n" .
            "Failed to delete the list of objects from testBucket with error: test message\n" .
            "Please fix error with object deletion before continuing.";
        $this->expectOutputString($expectedString);
        try {
            $this->service->deleteObjects('testBucket', ['testObject1', '$testObject2']);
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        }
    }

    public function testGetObject()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $testResult = $this->createMock(Result::class);
        $this->client->expects($this->exactly(2))->method('__call')->with('getObject')->will(
            $this->onConsecutiveCalls($testResult, $this->throwException($exception))
        );
        $object = $this->service->getObject('testBucket', 'testKey');
        $this->assertEquals($testResult, $object);
        try {
            $object = null;
            $object = $this->service->getObject('testBucket', 'testKey');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($object);
        }
    }

    public function testListObjects()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $testResult = $this->createMock(Result::class);
        $this->client->expects($this->exactly(2))->method('__call')->with('listObjectsV2')->will(
            $this->onConsecutiveCalls($testResult, $this->throwException($exception))
        );
        $objects = $this->service->listObjects('testBucket');
        $this->assertEquals($testResult, $objects);
        try {
            $this->service->listObjects('testBucket');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        }
    }

    public function testListAllObjects()
    {
        $testPaginator = $this->createMock(ResultPaginator::class);
        $this->client->expects($this->exactly(1))->method('getPaginator')->willReturn($testPaginator);
        $testPaginator->expects($this->exactly(4))->method('valid')->will(
            $this->onConsecutiveCalls(true, true, true, false)
        );
        $testPaginator->expects($this->exactly(3))->method('current')->will(
            $this->onConsecutiveCalls($this->startResult, $this->continueResult, $this->endResult)
        );
        $objects = $this->service->listAllObjects('testBucket');
        $this->assertEquals([1, 2, 3], $objects);
    }

    public function testCopyObject()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('copyObject')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception))
        );
        $this->assertNull($this->service->copyObject('testBucket', 'testKey', 'testSource'));
        try {
            $this->service->copyObject('testBucket', 'testKey', 'testSource');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        }
    }

    public function testEmptyAndDeleteBucket()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $testPaginator = $this->createMock(ResultPaginator::class);
        $this->client->expects($this->any())->method('getPaginator')->willReturn($testPaginator);
        $testPaginator->expects($this->exactly(5))->method('valid')->will(
            $this->onConsecutiveCalls(true, true, true, false, $this->throwException($exception))
        );
        $testPaginator->expects($this->exactly(3))->method('current')->will(
            $this->onConsecutiveCalls($this->startResult, $this->continueResult, $this->endResult)
        );
        $this->service->emptyAndDeleteBucket('testBucket');
        try {
            $this->service->emptyAndDeleteBucket('exceptionTest');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        }
    }

    public function testListBuckets()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('listBuckets')->will(
            $this->onConsecutiveCalls(['testBucket'], $this->throwException($exception))
        );
        $this->assertEquals(['testBucket'], $this->service->listBuckets());
        $bucketList = null;
        try {
            $bucketList = $this->service->listBuckets();
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($bucketList);
        }
    }

    public function testDeleteObject()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('deleteObject')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception))
        );
        $this->assertNull($this->service->deleteObject('testBucket', 'testKey'));
        $deletedObject = null;
        try {
            $deletedObject = $this->service->deleteObject('testBucket', 'testKey');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($deletedObject);
        }
    }

    public function testPutObject()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('putObject')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception))
        );
        $this->assertNull($this->service->putObject('testBucket', 'testKey'));
        $putObject = null;
        try {
            $putObject = $this->service->putObject('testBucket', 'testKey');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($putObject);
        }
    }

    public function testCreateBucket()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('createBucket')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception))
        );
        $this->assertNull($this->service->createBucket('testBucket'));
        $createBucket = null;
        try {
            $createBucket = $this->service->createBucket('testBucket');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($createBucket);
        }
    }

    public function testDeleteBucket()
    {
        $exception = new AwsException("test message", $this->createMock(CommandInterface::class));
        $this->client->expects($this->exactly(2))->method('__call')->with('deleteBucket')->will(
            $this->onConsecutiveCalls(true, $this->throwException($exception))
        );
        $this->assertNull($this->service->deleteBucket('testBucket'));
        $deleteBucket = null;
        try {
            $deleteBucket = $this->service->deleteBucket('testBucket');
        } catch (AwsException $e) {
            $this->assertEquals($exception, $e);
        } finally {
            $this->assertNull($deleteBucket);
        }
    }

    public function testVerbose()
    {
        $this->service->setVerbose(true);
        $this->assertTrue($this->service->isVerbose());
        $this->service->setVerbose(false);
        $this->assertFalse($this->service->isVerbose());
    }

    public function testPreSignedUrl()
    {
        $mockClient = new MockS3Client([]);
        $mockRequest = new class {
            public function getUri()
            {
                return "testUri.test";
            }
        };
        $mockClient->set("createPresignedRequest", $mockRequest);
        $this->service->setClient($mockClient);

        $commandMock = $this->createMock(CommandInterface::class);
        $expiration = 10;
        $preSignedUrl = $this->service->preSignedUrl($commandMock, $expiration);
        $this->assertEquals("testUri.test", $preSignedUrl);
    }
}
