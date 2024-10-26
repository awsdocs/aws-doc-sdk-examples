<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//
// Integration test runner for KmsBasics.php.
//

namespace Kms\tests;

use Aws\Kms\Exception\KmsException;
use Kms\KmsBasics;
use PHPUnit\Framework\TestCase;

require "vendor/autoload.php";
require_once __DIR__ . "/../KmsBasics.php";

/**
 * @group integ
 */
class KmsBasicsTests extends TestCase
{
    public static function setUpBeforeClass(): void
    {
        global $LINES;
        for($i = 0; $i < 28; ++$i){
            $LINES[] = "";
        }
        $LINES[] = "y";
    }

    /** @group integ */
    public function testItRunsWithoutThrowingAnException()
    {
        $start = new KmsBasics();
        try {
            $start->runExample();
            self::assertTrue(true); // Asserts that we made it to this line with no exceptions.
        }catch(KmsException $caught){
            echo "There was a problem running the test: {$caught->getAwsErrorMessage()}\n";
        }finally{
            $start->cleanUp();
        }
    }

    /** @group integ */
    public function testHelloService()
    {
        $start = new KmsBasics();
        try {
            $start->helloService();
            self::assertTrue(true); // Asserts that we made it to this line with no exceptions.
        }catch(KmsException $caught){
            echo "There was a problem running the hello service test: {$caught->getAwsErrorMessage()}\n";
        }
    }

}
