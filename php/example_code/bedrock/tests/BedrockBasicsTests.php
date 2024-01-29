<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for Amazon Bedrock files.
#

namespace bedrock\tests;

use Bedrock\BedrockService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class BedrockBasicsTests extends TestCase
{
    protected BedrockService $bedrockService;

    public function setup(): void
    {
        $this->clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $this->bedrockService = new BedrockService($this->clientArgs);
    }

    public function test_foundation_models_can_be_listed()
    {
        $result = $this->bedrockService->listFoundationModels();
        self::assertNotEmpty($result['modelSummaries']);
    }
}
