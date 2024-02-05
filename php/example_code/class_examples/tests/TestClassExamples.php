<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace ClassExamples;

use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class TestClassExamples extends TestCase
{
    public function testCommandPoolRuns()
    {
        include_once __DIR__ . "/../CommandPool.php";

        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }
}
