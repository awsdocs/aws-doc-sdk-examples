<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 * Shows how to use the AWS SDK for PHP with Amazon Bedrock to:
 * List information about the available foundation models.
 **/

// snippet-start:[php.example_code.bedrock.basics.scenario]
namespace Bedrock;

class GettingStartedWithBedrock
{
    protected BedrockService $bedrockService;
    public function runExample()
    {
        echo("\n");
        echo("--------------------------------------------------------------\n");
        print("Welcome to the Amazon Bedrock getting started demo using PHP!\n");
        echo("--------------------------------------------------------------\n");
        $bedrockService = new BedrockService();
        echo "Let's retrieve the available foundation models...\n";
        $models = $bedrockService->listFoundationModels();
        foreach ($models as $model) {
            echo "\n==========================================\n";
            echo " Model: {$model['modelId']}\n";
            echo "------------------------------------------\n";
            echo " Name: {$model['modelName']}\n";
            echo " Provider: {$model['providerName']}\n";
            echo " Input modalities: " . json_encode($model['inputModalities']) . "\n";
            echo " Output modalities: " . json_encode($model['outputModalities']) . "\n";
            echo " Supported customizations: " . json_encode($model['customizationsSupported']) . "\n";
            echo " Supported inference types: " . json_encode($model['inferenceTypesSupported']) . "\n";
            echo "==========================================\n";
        }
    }
}
// snippet-end:[php.example_code.bedrock.basics.scenario]
