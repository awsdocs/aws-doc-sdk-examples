<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
// Integration test runner for GettingStartedWithS3.php.
**/

namespace S3\tests;

use PHPUnit\Framework\TestCase;

/**
 * @group  integ
 * @covers \S3\GettingStartedWithS3
 * @covers \S3\S3Service
 */
class S3BasicsTest extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        include __DIR__ . "/../Runner.php";
        self::assertTrue(true); // This asserts that we made it to this line with no exceptions thrown.
    }
}
