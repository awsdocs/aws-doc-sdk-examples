<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * // Integration test runner for various S3 files.
 **/

namespace S3\tests;

use PHPUnit\Framework\TestCase;

/**
 * @group  integ
 * @covers \S3\S3Service
 */
class S3EtcTest extends TestCase
{
    /**
     * @covers \S3\PresignedURL
     */
    public function testPresignedUrl()
    {
        global $LINES;
        $LINES[] = "not-a-real-bucket";
        $LINES[] = "filename";

        include __DIR__ . "/../PresignedURL.php";
        self::assertTrue(true); // This asserts that we made it to this line with no exceptions thrown.
    }
}
