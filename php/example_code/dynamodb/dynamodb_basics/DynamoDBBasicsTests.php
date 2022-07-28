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
        global $LINES;
        $LINES[] = "test movie"; //Movie title
        $LINES[] = "2020";       //Release year
        $LINES[] = "5";          //Rating
        $LINES[] = "Some code was tested."; //Plot summary
        $LINES[] = "1";          //Fellowship rating
        $LINES[] = "1999";       //Birth year

        $start = new GettingStartedWithDynamoDB();
        $start->run();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }
}
