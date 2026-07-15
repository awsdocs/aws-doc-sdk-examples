<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.bedrock-runtime.service]
namespace BedrockRuntime;

use Aws\BedrockRuntime\BedrockRuntimeClient;
use AwsUtilities\AWSServiceClass;
use Exception;

class BedrockRuntimeService extends AWSServiceClass
{
    protected BedrockRuntimeClient $bedrockRuntimeClient;

    public function __construct(?BedrockRuntimeClient $client = null)
    {
        if ($client) {
            $this->bedrockRuntimeClient = $client;
        } else {
            $this->bedrockRuntimeClient = new BedrockRuntimeClient([
                'region' => 'us-west-2',
                'profile' => 'default'
            ]);
        }
    }

    public function getClient(): BedrockRuntimeClient
    {
        return $this->bedrockRuntimeClient;
    }

    // snippet-start:[php.example_code.bedrock-runtime.service.invokeClaude]
    public function invokeClaude($prompt)
    {
        // The different model providers have individual request and response formats.
        // For the format, ranges, and default values for Anthropic Claude, refer to:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html

        $completion = "";
        try {
            $modelId = 'global.anthropic.claude-haiku-4-5-20251001-v1:0';
        // Claude requires you to enclose the prompt as follows:
            $body = [
                'anthropic_version' => 'bedrock-2023-05-31',
                'max_tokens' => 512,
                'temperature' => 0.5,
                'messages' => [[
                    'role' => 'user',
                    'content' => $prompt
                ]]
            ];
            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);
            $response_body = json_decode($result['body']);
            $completion = $response_body->content[0]->text;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $completion;
    }
    // snippet-end:[php.example_code.bedrock-runtime.service.invokeClaude]

    // snippet-start:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]
    public function invokeStableDiffusion(string $prompt, int $seed = 0, string $aspect_ratio = '1:1')
    {
        // The different model providers have individual request and response formats.
        // For the format, ranges, and available parameters of Stable Diffusion models refer to:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-stability-diffusion.html

        $base64_image_data = "";
        try {
            $modelId = 'stability.stable-image-core-v1:1';
            $body = [
                'prompt' => $prompt,
                'aspect_ratio' => $aspect_ratio,
                'seed' => $seed,
                'output_format' => 'png',
            ];

            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);
            $response_body = json_decode($result['body']);
            $base64_image_data = $response_body->images[0];
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $base64_image_data;
    }
    // snippet-end:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]
}
// snippet-end:[php.example_code.bedrock-runtime.service]
