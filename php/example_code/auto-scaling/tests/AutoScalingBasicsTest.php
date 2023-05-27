<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for Amazon EC2 Auto Scaling files.
#

namespace AutoScaling\tests;

use AutoScaling\GettingStartedWithAutoScaling;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class AutoScalingBasicsTest extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        $test = new GettingStartedWithAutoScaling();
        $test->helloService();
        $test->runExample();
        $test->cleanUp();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }
}
