<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use BedrockAgentRuntime\GettingStartedWithBedrockAgentRuntime;

include __DIR__ . '/vendor/autoload.php';
include 'GettingStartedWithBedrockAgentRuntime.php';
try {
    $runner = new GettingStartedWithBedrockAgentRuntime();
    $runner->runExample();
} catch (Exception $e) {
    echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
}
