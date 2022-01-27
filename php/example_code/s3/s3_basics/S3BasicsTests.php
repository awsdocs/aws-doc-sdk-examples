<?php

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
