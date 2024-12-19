<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Integration tests for the Amazon Bedrock Agent Runtime service.
 */

namespace bedrockagentruntime\tests;

use Aws\BedrockAgentRuntime\BedrockAgentRuntimeClient;
use BedrockAgentRuntime\BedrockAgentRuntimeService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class bedrockAgentRuntimeService extends TestCase
{
    private BedrockAgentRuntimeService $bedrockAgentRuntimeService;
    private string $prompt = 'A test prompt';
    private string $agent_id = "FAKE_AGENT_ID";
    private string $agent_alias_id = "FAKE_AGENT_ALIAS_ID";
    public function setup(): void
    {
        $this->bedrockAgentRuntimeService = new BedrockAgentRuntimeService($this->agent_id,$this->agent_alias_id);
    }

    public function test_default_constructor_creates_client()
    {
        $service = new BedrockAgentRuntimeService($this->agent_id,$this->agent_alias_id);
        self::assertNotNull($service->getClient());
        self::assertEquals('us-east-1', $service->getClient()->getRegion());
    }

    public function test_constructor_uses_injected_client()
    {
        $client = new BedrockAgentRuntimeClient([
            'region' => 'us-west-2'
        ]);
        $service = new BedrockAgentRuntimeService($this->agent_id,$this->agent_alias_id,$client);
        self::assertNotNull($service->getClient());
        self::assertEquals($client, $service->getClient());
    }

    public function test_agent_can_be_invoked()
    {
        $sessionId = uniqid()
        $completion = $this->bedrockAgentRuntimeService->invokeAgent($this->prompt, $sessionId);
        self::assertNotEmpty($completion);
    }

  
}
