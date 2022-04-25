<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithS3.php.
#

namespace S3Basics;

use PHPUnit\Framework\TestCase;

class S3BasicsTests extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        include "GettingStartedWithS3.php";
        self::assertTrue(true); //this asserts that we made it to this line with no exceptions thrown
    }
}
