<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager;

use Aws\S3\S3Client;

function upload($payload)
{
    $s3Client = new S3Client([
        'region' => 'us-west-2',
        'version' => 'latest',
    ]);
    $fileName = implode(",", json_decode($payload['body'], true));
    $bucket = $_ENV['STORAGE_BUCKET_NAME'];

    $presigned = getPresignedRequest($s3Client, $bucket, $fileName);

    return [
        'url' => $presigned,
    ];
}

/**
 * @param S3Client $s3Client
 * @param string $bucket
 * @param string $fileName
 * @param int $minutes
 * @return string
 */
function getPresignedRequest(S3Client $s3Client, string $bucket, string $fileName, int $minutes = 15): string
{
    $key = uniqid() . $fileName;
    $command = $s3Client->getCommand('PutObject', [
        'Bucket' => $bucket,
        'Key' => $key,
    ]);

    return (string)$s3Client->createPresignedRequest($command, "$minutes minutes")->getUri();
}
