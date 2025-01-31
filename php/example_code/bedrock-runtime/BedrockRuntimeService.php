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
    public function __construct(BedrockRuntimeClient $client = null)
    {
        if ($client) {
            $this->bedrockRuntimeClient = $client;
        } else {
            $this->bedrockRuntimeClient = new BedrockRuntimeClient([
                'region' => 'us-east-1',
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
            $modelId = 'anthropic.claude-3-haiku-20240307-v1:0';
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

    // snippet-start:[php.example_code.bedrock-runtime.service.invokeJurassic2]
    public function invokeJurassic2($prompt)
    {
        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-jurassic2.html

        $completion = "";
        try {
            $modelId = 'ai21.j2-mid-v1';
            $body = [
                'prompt' => $prompt,
                'temperature' => 0.5,
                'maxTokens' => 200,
            ];
            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);
            $response_body = json_decode($result['body']);
            $completion = $response_body->completions[0]->data->text;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $completion;
    }
    // snippet-end:[php.example_code.bedrock-runtime.service.invokeJurassic2]

    // snippet-start:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]
    public function invokeStableDiffusion(string $prompt, int $seed, string $style_preset)
    {
        // The different model providers have individual request and response formats.
        // For the format, ranges, and available style_presets of Stable Diffusion models refer to:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-stability-diffusion.html

        $base64_image_data = "";
        try {
            $modelId = 'stability.stable-diffusion-xl-v1';
            $body = [
                'text_prompts' => [
                    ['text' => $prompt]
                ],
                'seed' => $seed,
                'cfg_scale' => 10,
                'steps' => 30
            ];
            if ($style_preset) {
                $body['style_preset'] = $style_preset;
            }

            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);
            $response_body = json_decode($result['body']);
            $base64_image_data = $response_body->artifacts[0]->base64;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $base64_image_data;
    }
    // snippet-end:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]

    // snippet-start:[php.example_code.bedrock-runtime.service.invokeTitanImage]
    public function invokeTitanImage(string $prompt, int $seed)
    {
        // The different model providers have individual request and response formats.
        // For the format, ranges, and default values for Titan Image models refer to:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-image.html

        $base64_image_data = "";
        try {
            $modelId = 'amazon.titan-image-generator-v1';
            $request = json_encode([
                'taskType' => 'TEXT_IMAGE',
                'textToImageParams' => [
                    'text' => $prompt
                ],
                'imageGenerationConfig' => [
                    'numberOfImages' => 1,
                    'quality' => 'standard',
                    'cfgScale' => 8.0,
                    'height' => 512,
                    'width' => 512,
                    'seed' => $seed
                ]
            ]);
            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => $request,
                'modelId' => $modelId,
            ]);
            $response_body = json_decode($result['body']);
            $base64_image_data = $response_body->images[0];
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $base64_image_data;
    }
    // snippet-end:[php.example_code.bedrock-runtime.service.invokeTitanImage]
}
// snippet-end:[php.example_code.bedrock-runtime.service]
