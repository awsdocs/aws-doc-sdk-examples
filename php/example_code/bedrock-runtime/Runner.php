<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use BedrockRuntime\GettingStartedWithBedrockRuntime;

include __DIR__ . '/vendor/autoload.php';
include 'GettingStartedWithBedrockRuntime.php';
try {
    $runner = new GettingStartedWithBedrockRuntime();
    $runner->runExample();
} catch (Exception $e) {
    echo "Error: ({$e->getCode()}) - {$e->getMessage()}\n";
}
