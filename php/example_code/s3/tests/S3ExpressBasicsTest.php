<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//
// Integration test runner for S3ExpressBasics.php.
//

namespace S3\tests;

use Aws\CloudFormation\CloudFormationClient;
use Aws\S3\Exception\S3Exception;
use Aws\S3\S3Client;
use PHPUnit\Framework\TestCase;
use S3\S3ExpressBasics;
use S3\S3Service;

/**
 * @group  integ
 * @covers \S3\S3ExpressBasics
 * @covers \S3\S3Service
 */
class S3ExpressBasicsTest extends TestCase
{
    public static function setUpBeforeClass(): void
    {
        global $LINES;
        $LINES[] = "";
        $LINES[] = "y";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = 1;
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "";
        $LINES[] = "y";
    }

    public function testItRunsWithoutThrowingAnException()
    {
        try {
            $start = new S3ExpressBasics();
            $start->runExample();
            self::assertTrue(true); // This asserts that we made it to this line with no exceptions thrown.
        }catch(S3Exception $caught){
            echo "There was a problem running the tests: {$caught->getAwsErrorMessage()}\n";
        }finally{
            $start->cleanUp();
        }
    }
}
