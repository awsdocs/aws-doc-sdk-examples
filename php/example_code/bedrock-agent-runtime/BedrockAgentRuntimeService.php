<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.bedrock-agent-runtime.service]
namespace BedrockAgentRuntime;

use Aws\BedrockAgentRuntime\BedrockAgentRuntimeClient;
use AwsUtilities\AWSServiceClass;
use Exception;

class BedrockAgentRuntimeService extends AWSServiceClass
{
    public function __construct(
        string $agentId,
        string $agentAliasId,
        BedrockAgentRuntimeClient $client = null
    ) {
        if ($client) {
            $this->bedrockAgentRuntimeClient = $client;
        } else {
            $this->bedrockAgentRuntimeClient = new BedrockAgentRuntimeClient([
                'region' => 'us-east-1',
                'profile' => 'default'
            ]);
        }
        $this->agentId = $agentId;
        $this->agentAliasId = $agentAliasId;
    }

    public function getClient(): BedrockAgentRuntimeClient
    {
        return $this->bedrockAgentRuntimeClient;
    }

    // snippet-start:[php.example_code.bedrock-agent-runtime.service.invokeClaude]
    public function invokeAgent(string $prompt, string $sessionId): string {

        // The Amazon Bedrock Agents Runtime API provides more parameters.
        // For example to filter knowledge base; for the full list of parameters refer to:
        // https://docs.aws.amazon.com/aws-sdk-php/v3/api/api-bedrock-agent-runtime-2023-07-26.html#invokeagent

        $payload = [
            'agentId' => $this->agentId,
            'agentAliasId' => $this->agentAliasId,
            'sessionId' => $sessionId,
            'enableTrace' => false,
            'inputText' => $prompt
        ];
        
        $completion = "";
        
        try {
            $response = $this->bedrockAgentRuntimeClient->invokeAgent($payload);
            
            foreach ($response['completion'] as $chunkEvent) {
                $chunk = $chunkEvent['chunk'];
                $decodedResponse = utf8_decode($chunk['bytes']);
                $completion .= $decodedResponse;
            }
            
            return $completion;
            
        } catch (AwsException $e) {
            throw new Exception("Bedrock Agent invocation failed: " . $e->getMessage());
        }
    }
    // snippet-end:[php.example_code.bedrock-agent-runtime.service.invokeClaude]

}
// snippet-end:[php.example_code.bedrock-agent-runtime.service]
