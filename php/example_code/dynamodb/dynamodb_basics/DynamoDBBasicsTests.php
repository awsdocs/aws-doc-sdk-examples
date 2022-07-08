<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithDynamoDB.php.
#

namespace DynamoDb\Basics;

use PHPUnit\Framework\TestCase;

class DynamoDBBasicsTests extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        global $argv;
        $argv[] = "test movie"; //Movie title
        $argv[] = "2020";       //Release year
        $argv[] = "5";          //Rating
        $argv[] = "Some code was tested."; //Plot summary
        $argv[] = "1";          //Fellowship rating
        $argv[] = "1999";       //Birth year

        $start = new GettingStartedWithDynamoDB();
        $start->run();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }
}
