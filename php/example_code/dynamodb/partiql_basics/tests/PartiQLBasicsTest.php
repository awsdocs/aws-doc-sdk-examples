<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for GettingStartedWithPartiQL.php and GettingStartedWithPartiQLBatch.php.
#

namespace DynamoDb\PartiQL_Basics;

use PHPUnit\Framework\TestCase;

class PartiQLBasicsTest extends TestCase
{
    public function testItRunsWithoutThrowingAnException()
    {
        global $LINES;
        $LINES[] = "partiql test movie"; //Movie title
        $LINES[] = "2020";       //Release year
        $LINES[] = "5";          //Rating
        $LINES[] = "Some code was tested."; //Plot summary
        $LINES[] = "1";          //Fellowship rating
        $LINES[] = "1999";       //Birth year
        $start = new GettingStartedWithPartiQL();
        $start->run();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }

    public function testBatchRunsWithoutThrowingAnException()
    {
        global $LINES;
        $LINES[] = "pq batch test movie"; //Movie title
        $LINES[] = "2020";       //Release year
        $LINES[] = "5";          //Rating
        $LINES[] = "Some code was tested."; //Plot summary
        $LINES[] = "1";          //Fellowship rating
        $LINES[] = "1999";       //Birth year
        $start = new GettingStartedWithPartiQLBatch();
        $start->run();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }
}
