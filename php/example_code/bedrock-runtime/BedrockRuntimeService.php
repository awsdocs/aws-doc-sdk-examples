<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#snippet-start:[php.example_code.bedrock-runtime.service]

namespace BedrockRuntime;

use Aws\BedrockRuntime\BedrockRuntimeClient;

class BedrockRuntimeService extends \AwsUtilities\AWSServiceClass 
{
    protected BedrockRuntimeClient $bedrockRuntimeClient;

    public function __construct(
        $client = null,
        $region = 'us-east-1',
        $version = 'latest',
        $profile = 'default'
    ) {
        if (gettype($client) == BedrockRuntimeClient::class) {
            $this->bedrockRuntimeClient = $client;
            return;
        }
        $this->bedrockRuntimeClient = new BedrockRuntimeClient([
            'region' => $region,
            'version' => $version,
            'profile' => $profile,
        ]);
    }

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeClaude]
    public function invokeClaude($prompt) {

        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Anthropic Claude, refer to:
        # https://docs.anthropic.com/claude/reference/complete_post

        try {
            
            # Claude requires you to enclose the prompt as follows:
            $prompt = "Human: " . $prompt . "\n\nAssistant:";

            $body = (object) [
                'prompt' => $prompt,
                'max_tokens_to_sample' => 200,
                'temperature' => 0.5,
                "stop_sequences" => ["\n\nHuman:"],
            ];

            $result = $this->bedrockRuntimeClient->invokeModel([
                'body' => json_encode($body),
                'modelId' => 'anthropic.claude-v2',
                'contentType' => 'application/json',
            ]);

            $response_body = json_decode($result["body"]);

            $completion = $response_body->completion;

            return $completion;

        } catch (Exception $e) {
            echo "Error: (" . $e->getCode() . ") - " . $e->getMessage() . "\n";
        }
    }
    #snippet-start:[php.example_code.bedrock-runtime.service.invokeClaude]
}

#snippet-start:[php.example_code.bedrock-runtime.service]
