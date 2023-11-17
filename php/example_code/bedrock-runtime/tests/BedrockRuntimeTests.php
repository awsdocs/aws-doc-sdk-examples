<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration tests for the Amazon Bedrock Runtime service.
#

namespace bedrockruntime\tests;

use BedrockRuntime\BedrockRuntimeService;
use Aws\BedrockRuntime\BedrockRuntimeClient;
use PHPUnit\Framework\TestCase;

require_once __DIR__ . "/../BedrockRuntimeService.php";

/**
 * @group integ
 */

 class BedrockRuntimeTests extends TestCase {

    protected BedrockRuntimeService $bedrockRuntimeService;

    public function setup(): void
    {
        $this->clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $this->bedrockRuntimeService = new BedrockRuntimeService($this->clientArgs);
    }

    public function test_claude_can_be_invoked()
    {
        $prompt = "A test prompt";
        $completion = $this->bedrockRuntimeService->invokeClaude($prompt);
        self::assertNotEmpty($completion);
    }

    public function test_jurassic2_can_be_invoked()
    {
        $prompt = "A test prompt";
        $completion = $this->bedrockRuntimeService->invokeJurassic2($prompt);
        self::assertNotEmpty($completion);
    }
 }
