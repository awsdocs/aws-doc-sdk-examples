<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Integration tests for the Amazon Bedrock Runtime service.
 */

namespace bedrockruntime\tests;

use Aws\BedrockRuntime\BedrockRuntimeClient;
use BedrockRuntime\BedrockRuntimeService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class BedrockRuntimeTests extends TestCase
{
    private BedrockRuntimeService $bedrockRuntimeService;
    private string $prompt = 'A test prompt';
    public function setup(): void
    {
        $this->bedrockRuntimeService = new BedrockRuntimeService();
    }

    public function test_default_constructor_creates_client()
    {
        $service = new BedrockRuntimeService();
        self::assertNotNull($service->getClient());
        self::assertEquals('us-east-1', $service->getClient()->getRegion());
    }

    public function test_constructor_uses_injected_client()
    {
        $client = new BedrockRuntimeClient([
            'region' => 'us-west-2'
        ]);
        $service = new BedrockRuntimeService($client);
        self::assertNotNull($service->getClient());
        self::assertEquals($client, $service->getClient());
    }

    public function test_claude_can_be_invoked()
    {
        $completion = $this->bedrockRuntimeService->invokeClaude($this->prompt);
        self::assertNotEmpty($completion);
    }

    public function test_jurassic2_can_be_invoked()
    {
        $completion = $this->bedrockRuntimeService->invokeJurassic2($this->prompt);
        self::assertNotEmpty($completion);
    }

    public function test_stable_diffusion_can_be_invoked()
    {
        $seed = 0;
        $style_preset = "photographic";
        $base64_image_data = $this->bedrockRuntimeService->invokeStableDiffusion($this->prompt, $seed, $style_preset);
        self::assertNotEmpty($base64_image_data);
    }

    public function test_titan_image_can_be_invoked()
    {
        $seed = 0;
        $base64_image_data = $this->bedrockRuntimeService->invokeTitanImage($this->prompt, $seed);
        self::assertNotEmpty($base64_image_data);
    }
}
