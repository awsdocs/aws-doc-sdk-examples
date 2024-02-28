<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager;

use Aws\Rekognition\RekognitionClient;
use Aws\DynamoDb\DynamoDbClient;

function detectLabels($payload)
{
    $clientConfig = [
        'region' => 'us-west-2',
        'version' => 'latest',
    ];
    $DDBClient = new DynamoDbClient($clientConfig);
    $rekognitionClient = new RekognitionClient($clientConfig);

    list($s3KeyName, $labels) = getLabels($rekognitionClient, $payload['Records'][0]['s3']);

    updateLabels($DDBClient, $labels, $s3KeyName);

    return [
        'success' => 'true',
    ];
}

/**
 * @param DynamoDbClient $DDBClient
 * @param mixed $labels
 * @param mixed $s3KeyName
 * @return void
 */
function updateLabels(DynamoDbClient $DDBClient, array $labels, string $s3KeyName): void
{
    foreach ($labels as $label) {
        $DDBClient->updateItem([
            'TableName' => $_ENV['LABELS_TABLE_NAME'],
            'Key' => [
                'Label' => ["S" => $label['Name']],
            ],
            'UpdateExpression' =>
                "SET #Count = if_not_exists(#Count, :zero) + :one, " .
                "Images = list_append(if_not_exists(Images, :empty), :images)",
            'ExpressionAttributeValues' => [
                ":zero" => ["N" => "0"],
                ":one" => ["N" => "1"],
                ":empty" => ["L" => []],
                ":images" => ["L" => [["S" => $s3KeyName]]],
            ],
            'ExpressionAttributeNames' => [
                "#Count" => "Count",
            ],
        ]);
    }
}

/**
 * @param RekognitionClient $rekognitionClient
 * @param array $s3Result
 * @param int $confidence
 * @return array
 */
function getLabels(RekognitionClient $rekognitionClient, array $s3Result, int $confidence = 95): array
{
    $s3KeyName = urldecode($s3Result['object']['key']);
    $s3BucketName = urldecode($s3Result['bucket']['name']);
    $result = $rekognitionClient->detectLabels([
        'Image' => [
            'S3Object' => [
                'Bucket' => $s3BucketName,
                'Name' => $s3KeyName,
            ],
        ],
    ]);
    $labels = array_filter($result['Labels'], function ($label) use ($confidence) {
        return $label['Confidence'] >= $confidence;
    });
    return array($s3KeyName, $labels);
}
