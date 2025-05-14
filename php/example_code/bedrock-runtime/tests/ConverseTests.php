<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntime\tests;

use BedrockRuntime\Models\AmazonNova;
use PHPUnit\Framework\TestCase;

class ConverseTests extends TestCase
{
    public function test_amazon_nova_text(): void
    {
        $testObject = new AmazonNova\Text\Converse();
        $result = $testObject->converse();
        self::assertIsString($result);
        self::assertNotEmpty($result);
    }
}
