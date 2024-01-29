<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager\Tests;

use Aws\DynamoDb\DynamoDbClient;
use phpmock\phpunit\PHPMock;
use PHPUnit\Framework\TestCase;

use function PhotoAssetManager\updateLabels;

/**
 * @group unit
 */
class PAMDetectLabelsTest extends TestCase
{
    use PHPMock;

    public static function setUpBeforeClass(): void
    {
        include __DIR__ . "/../src/detectLabels.php";
    }

    public function testUpdateLabels()
    {
        $DDBClient = $this->createMock(DynamoDbClient::class);
        $DDBClient->expects($this->exactly(2))->method('__call')->with('updateItem');
        $_ENV['LABELS_TABLE_NAME'] = "testTableName";
        $labels = ["one" => ['Name' => 'labelOne'], "two" => ['Name' => 'labelTwo']];
        $s3KeyName = "testKeyName";
        updateLabels($DDBClient, $labels, $s3KeyName);
    }
}
