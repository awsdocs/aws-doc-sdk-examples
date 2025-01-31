<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AwsUtilities;

use Aws\CommandInterface;
use Aws\S3\S3Client;

/**
 * This class allows you to mock any AWSClient even when PHPUnit fails due to hidden, dynamic, or private
 * method calls which cannot be mocked normally.
 *
 * WARNING: This class *does not* do any type checking, so if you set the return values to an incompatible data type,
 * your tests will fail spectacularly.
 */
class MockS3Client extends S3Client {

    protected array $returnValues = [];

    public function set($methodName, $returnValue){
        $this->returnValues[$methodName] = $returnValue;
    }

    public function __construct()
    {

    }

    public function __call($name, array $arguments)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getCommand($name, array $args = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function execute(CommandInterface $command)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function executeAsync(CommandInterface $command)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getCredentials()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getRegion()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getEndpoint()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getApi()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getConfig($option = null)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getHandlerList()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getIterator($name, array $args = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getPaginator($name, array $args = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function waitUntil($name, array $args = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getWaiter($name, array $args = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function createPresignedRequest(CommandInterface $command, $expires, array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function getObjectUrl($bucket, $key)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function doesBucketExist($bucket)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function doesBucketExistV2($bucket, $accept403 = false)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function doesObjectExist($bucket, $key, array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function doesObjectExistV2($bucket, $key, $includeDeleteMarkers = false, array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function registerStreamWrapper()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function registerStreamWrapperV2()
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function deleteMatchingObjects($bucket, $prefix = '', $regex = '', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function deleteMatchingObjectsAsync($bucket, $prefix = '', $regex = '', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function upload($bucket, $key, $body, $acl = 'private', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function uploadAsync($bucket, $key, $body, $acl = 'private', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function copy($fromBucket, $fromKey, $destBucket, $destKey, $acl = 'private', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function copyAsync($fromBucket, $fromKey, $destBucket, $destKey, $acl = 'private', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function uploadDirectory($directory, $bucket, $keyPrefix = null, array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function uploadDirectoryAsync($directory, $bucket, $keyPrefix = null, array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function downloadBucket($directory, $bucket, $keyPrefix = '', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function downloadBucketAsync($directory, $bucket, $keyPrefix = '', array $options = [])
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function determineBucketRegion($bucketName)
    {
        return $this->returnValues[__FUNCTION__];
    }

    public function determineBucketRegionAsync($bucketName)
    {
        return $this->returnValues[__FUNCTION__];
    }
}
