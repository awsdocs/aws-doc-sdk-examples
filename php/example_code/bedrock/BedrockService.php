<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[php.example_code.bedrock.service]
namespace Bedrock;

use Aws\Bedrock\BedrockClient;
use AwsUtilities\AWSServiceClass;

class BedrockService extends AWSServiceClass
{
    // snippet-start:[php.example_code.bedrock.service.listFoundationModels]
    public function listFoundationModels()
    {
        $bedrockClient = new BedrockClient([
            'region' => 'us-west-2',
            'profile' => 'default'
        ]);
        $response = $bedrockClient->listFoundationModels();
        return $response['modelSummaries'];
    }
    // snippet-end:[php.example_code.bedrock.service.listFoundationModels]
}
// snippet-end:[php.example_code.bedrock.service]
