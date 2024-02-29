<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager;

use Aws\DynamoDb\DynamoDbClient;
use Aws\Result;
use Aws\S3\S3Client;
use Aws\Sns\SnsClient;
use ZipArchive;

function download($payload)
{
    $clientConfig = [
        'region' => 'us-west-2',
        'version' => 'latest',
    ];

    $labels = formatLabels($payload['labels']);

    $DDBClient = new DynamoDbClient($clientConfig);
    $tableName = getenv("LABELS_TABLE_NAME");
    $images = getImages($DDBClient, $tableName, $labels);

    $s3Client = new S3Client($clientConfig);
    $storageBucket = $_ENV['STORAGE_BUCKET_NAME'];
    $workingBucket = $_ENV['WORKING_BUCKET_NAME'];
    $zipName = createZipArchive($s3Client, $images['Responses'], $storageBucket, $workingBucket, $tableName);

    $presigned = createAPresignedUrlToDownloadTheZip($s3Client, $workingBucket, $zipName);

    $snsClient = new SnsClient($clientConfig);
    $message = "Your images are ready for download at the following URL.\n" .
        "Amazon SNS breaks up the long URL. Strip out the whitespace characters " .
        "get the correct link.\n $presigned";
    $topic = getenv("NOTIFICATION_TOPIC");
    $snsClient->Publish([
        "Message" => $message,
        "TopicArn" => $topic,
    ]);
    return [
        "success" => true,
    ];
}

/**
 * @param S3Client $s3Client
 * @param string $bucket
 * @param string $zipName
 * @return string
 */
function createAPresignedUrlToDownloadTheZip(S3Client $s3Client, string $bucket, string $zipName): string
{
    $command = $s3Client->getCommand('GetObject', [
        'Bucket' => $bucket,
        'Key' => $zipName,
    ]);
    return (string)$s3Client->createPresignedRequest($command, "15 minutes")->getUri();
}

/**
 * @param S3Client $s3Client
 * @param array $responses
 * @param string $storageBucket
 * @param string $workingBucket
 * @param string $tableName
 * @param ZipArchive $archive
 * @param int $create
 * @return string
 */
function createZipArchive(
    S3Client $s3Client,
    array $responses,
    string $storageBucket,
    string $workingBucket,
    string $tableName,
    ZipArchive $archive = new ZipArchive(),
    int $create = ZipArchive::CREATE,
): string {
    $s3Client->registerStreamWrapper();
    $zipName = uniqid() . ".zip";
    $zipPath = "/tmp/$zipName";
    $archive->open($zipPath, $create);
    foreach ($responses[$tableName] as $requests) {
        foreach ($requests['Images']['L'] as $attribute) {
            foreach ($attribute as $image) {
                $fileString = file_get_contents("s3://$storageBucket/$image");
                $archive->addFromString($image, $fileString);
            }
        }
    }
    $archivePath = $archive->filename;
    $archive->close();
    $s3Client->putObject([
        "Bucket" => $workingBucket,
        "Key" => $zipName,
        "SourceFile" => $archivePath,
    ]);
    return $zipName;
}

/**
 * @param DynamoDbClient $DDBClient
 * @param string $tableName
 * @param array $labels
 * @return Result
 */
function getImages(DynamoDbClient $DDBClient, string $tableName, array $labels)
{
    return $DDBClient->batchGetItem([
        "RequestItems" => [
             $tableName => [
                "Keys" => $labels,
                "ProjectionExpression" => "Images",
             ],
        ],
    ]);
}

/**
 * @param array $rawLabels
 * @return array
 */
function formatLabels(array $rawLabels): array
{
    $labels = [];
    foreach ($rawLabels as $label) {
        $labels[] = ["Label" => ["S" => $label]];
    }
    return $labels;
}
