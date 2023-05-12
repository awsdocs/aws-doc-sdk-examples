<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithS3.php.
#

namespace S3Basics\tests;

use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class S3BasicsTests extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        include __DIR__ . "/../GettingStartedWithS3.php";
        self::assertTrue(true); // This asserts that we made it to this line with no exceptions thrown.
    }
}
