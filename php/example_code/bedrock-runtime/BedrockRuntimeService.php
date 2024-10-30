<?php
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#snippet-start:[php.example_code.bedrock-runtime.service]

namespace BedrockRuntime;

use Aws\BedrockRuntime\BedrockRuntimeClient;
use Exception;

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
    public function invokeClaude($prompt)
    {
        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Anthropic Claude, refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html

        $completion = "";

        try {
            $modelId = 'anthropic.claude-v2';

            # Claude requires you to enclose the prompt as follows:
            $prompt = "\n\nHuman: {$prompt}\n\nAssistant:";

            $body = [
                'prompt' => $prompt,
                'max_tokens_to_sample' => 200,
                'temperature' => 0.5,
                'stop_sequences' => ["\n\nHuman:"],
            ];

            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);

            $response_body = json_decode($result['body']);

            $completion = $response_body->completion;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $completion;
    }
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeClaude]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeJurassic2]
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
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeJurassic2]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeLlama2]
    public function invokeLlama2($prompt)
    {
        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html

        $completion = "";

        try {
            $modelId = 'meta.llama2-13b-chat-v1';

            $body = [
                'prompt' => $prompt,
                'temperature' => 0.5,
                'max_gen_len' => 512,
            ];

            $result = $this->bedrockRuntimeClient->invokeModel([
                'contentType' => 'application/json',
                'body' => json_encode($body),
                'modelId' => $modelId,
            ]);

            $response_body = json_decode($result['body']);

            $completion = $response_body->generation;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }

        return $completion;
    }
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeLlama2]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]
    public function invokeStableDiffusion(string $prompt, int $seed, string $style_preset)
    {
        # The different model providers have individual request and response formats.
        # For the format, ranges, and available style_presets of Stable Diffusion models refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-stability-diffusion.html

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
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeStableDiffusion]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeTitanImage]
    public function invokeTitanImage(string $prompt, int $seed)
    {
        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Titan Image models refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-image.html

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
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeTitanImage]
}

#snippet-end:[php.example_code.bedrock-runtime.service]
