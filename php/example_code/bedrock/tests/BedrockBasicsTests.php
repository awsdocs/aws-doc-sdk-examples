<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Integration test runner for Amazon Bedrock files.
 */

namespace bedrock\tests;

use Bedrock\BedrockService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class BedrockBasicsTests extends TestCase
{
    protected BedrockService $bedrockService;
    public function test_foundation_models_can_be_listed()
    {
        $this->bedrockService = new BedrockService();
        $result = $this->bedrockService->listFoundationModels();
        self::assertNotEmpty($result);
    }
}
