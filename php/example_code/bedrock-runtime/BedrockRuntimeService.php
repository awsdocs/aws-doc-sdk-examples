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
    public function invokeClaude($prompt)
    {

        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Anthropic Claude, refer to:
        # https://docs.anthropic.com/claude/reference/complete_post

        try {
            $modelId = 'anthropic.claude-v2';

            # Claude requires you to enclose the prompt as follows:
            $prompt = 'Human: {$prompt}\n\nAssistant:';

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

            return $completion;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }
    }
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeClaude]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeJurassic2]
    public function invokeJurassic2($prompt)
    {

        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
        # https://docs.ai21.com/reference/j2-complete-ref

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

            return $completion;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }
    }
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeJurassic2]

    #snippet-start:[php.example_code.bedrock-runtime.service.invokeLlama2]
    public function invokeLlama2($prompt)
    {

        # The different model providers have individual request and response formats.
        # For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
        # https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html

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

            return $completion;
        } catch (Exception $e) {
            echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
        }
    }
    #snippet-end:[php.example_code.bedrock-runtime.service.invokeLlama2]
}

#snippet-end:[php.example_code.bedrock-runtime.service]
