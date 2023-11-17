<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#snippet-start:[php.example_code.bedrock.service]
namespace Bedrock;

use Aws\Bedrock\BedrockClient;

class BedrockService extends \AwsUtilities\AWSServiceClass
{
    protected BedrockClient $bedrockClient;

    public function __construct(
        $client = null,
        $region = 'us-east-1',
        $version = 'latest',
        $profile = 'default'
    ) {
        if (gettype($client) == BedrockClient::class) {
            $this->bedrockClient = $client;
            return;
        }
        $this->bedrockClient = new BedrockClient([
            'region' => $region,
            'version' => $version,
            'profile' => $profile,
        ]);
    }

    #snippet-start:[php.example_code.bedrock.service.listFoundationModels]
    public function listFoundationModels() {
        $result = $this->bedrockClient->listFoundationModels();
        return $result;
    }
    #snippet-end:[php.example_code.bedrock.service.listFoundationModels]
}
#snippet-end:[php.example_code.bedrock.service]
