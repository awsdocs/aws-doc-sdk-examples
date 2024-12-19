<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 * Shows how to use the AWS SDK for PHP with the Amazon Bedrock Agent Runtime to:
 * - chat with an agent
 **/

// snippet-start:[php.example_code.bedrock-agent-runtime.basics.scenario]
namespace BedrockAgentRuntime;

class GettingStartedWithBedrockAgentRuntime
{
    protected BedrockAgentRuntimeService $bedrockAgentRuntimeService;
    public function runExample()
    {
        echo "\n";
        echo "---------------------------------------------------------------------\n";
        echo "Welcome to the Amazon Bedrock Agent Runtime getting started demo using PHP!\n";
        echo "---------------------------------------------------------------------\n";
        $agent_id = "YOUR_AGENT_ID"
        $agent_alias_id = "YOUR_AGENT_ALIAS_ID"
        $sessionId = uniqid()
        $bedrockAgentRuntimeService = new BedrockAgentRuntimeService($agent_id,$agent_alias_id);
        $prompt = "Hello, I am Mateo. Who are you?"
        echo "\nSession ID: " . $sessionId;
        echo "\nInput Text: " . $prompt;
        echo "\n\nAgent:";
        echo $bedrockAgentRuntimeService->invokeAgent($prompt, $sessionId);
        $prompt = "Do you remember my name?"
        echo "\nSession ID: " . $sessionId;
        echo "\nInput Text: " . $prompt;
        echo "\n\nAgent:";
        echo $bedrockAgentRuntimeService->invokeAgent($prompt, $sessionId);
    }
}
// snippet-end:[php.example_code.bedrock-agent-runtime.basics.scenario]
