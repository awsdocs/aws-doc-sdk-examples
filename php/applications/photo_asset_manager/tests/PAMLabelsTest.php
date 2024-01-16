<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace PhotoAssetManager\Tests;

use Aws\DynamoDb\DynamoDbClient;
use phpmock\phpunit\PHPMock;
use PHPUnit\Framework\TestCase;

use function PhotoAssetManager\getAllLabels;

/**
 * @group unit
 */
class PAMLabelsTest extends TestCase
{
    use PHPMock;

    public static function setUpBeforeClass(): void
    {
        include __DIR__ . "/../src/labels.php";
    }

    public function testGetAllLabels()
    {
        $DDBClient = $this->createMock(DynamoDbClient::class);
        $results = [
            'Items' => [0 => [
                'Label' => ['S' => 'testLabel'],
                'Count' => ['N' => 1]]
            ],
            'LastEvaluatedKey' => null,
        ];
        $DDBClient->expects($this->once())->method('__call')->with('scan')->willReturn($results);
        $parameters = [];

        $test = getAllLabels($DDBClient, $parameters);
        $expected = ['labels' => ['testLabel' => ['count' => 1]]];

        $this->assertEquals($expected, $test);
    }
}
