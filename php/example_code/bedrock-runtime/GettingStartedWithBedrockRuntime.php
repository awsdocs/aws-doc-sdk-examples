<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 * Shows how to use the AWS SDK for PHP with the Amazon Bedrock Runtime to:
 * - ...
 **/

# snippet-start:[php.example_code.bedrock-runtime.basics.scenario]
namespace BedrockRuntime;

class GettingStartedWithBedrockRuntime 
{
    protected BedrockRuntimeService $bedrockRuntimeService;

    public function runExample()
    {
        echo("\n");
        echo("----------------------------------------------------------------------\n");
        print("Welcome to the Amazon Bedrock Runtime getting started demo using PHP!\n");
        echo("----------------------------------------------------------------------\n");

        $clientArgs = [
            'region' => 'us-east-1',
            'version' => 'latest',
            'profile' => 'default',
        ];

        $bedrockRuntimeService = new BedrockRuntimeService($clientArgs);

        $prompt = "In one paragraph, who are you?";

        echo "\nPrompt: " . $prompt;

        echo "\n\nAnthropic Claude:";
        echo $bedrockRuntimeService->invokeClaude($prompt);
        
        echo "\n\nAI21 Labs Jurassic-2: ";
        echo $bedrockRuntimeService->invokeJurassic2($prompt);
        
        echo "\n\nMeta Llama 2 Chat: ";
        echo $bedrockRuntimeService->invokeLlama2($prompt);
    }

}

# snippet-end:[php.example_code.bedrock-runtime.basics.scenario]
