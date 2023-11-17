<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace bedrock\tests;

use Bedrock\BedrockService;
use PHPUnit\Framework\TestCase;

require_once __DIR__ . "/../BedrockService.php";

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
        self::assertNotEmpty($result["modelSummaries"]);
    }

    public function test_foundation_models_can_be_filtered_by_provider_name()
    {
        $result = $this->bedrockService->listFoundationModels($providerName = "amazon");
        foreach ($result["modelSummaries"] as $model) {
            self::assertEquals("Amazon", $model["providerName"]);
        }
    }
}