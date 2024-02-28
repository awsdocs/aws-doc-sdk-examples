<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use Aws\S3\S3Client;

function hello($data)
{
    $dataResult = var_export($data, true);

    $bucket = $data['Records'][0]['s3']['bucket']['name'];
    $key = $data['Records'][0]['s3']['object']['key'];

    $s3Client = new S3Client(['region' => 'us-west-2']);
    $contentType = $s3Client->headObject(['Bucket' => $bucket, 'Key' => $key])['ContentType'];

    return [
        'bucket' => $bucket,
        'key' => $key,
        'contentType' => $contentType,
        'dataResult' => $dataResult
    ];
}
