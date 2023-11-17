<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use Bedrock\GettingStartedWithBedrock;
use AwsUtilities\RunnableExample;

include __DIR__ . "/vendor/autoload.php";

include "GettingStartedWithBedrock.php";

try {
    $runner = new GettingStartedWithBedrock();
    $runner->runExample();
} catch (Exception $e) {
    echo "Error: (" . $e->getCode() . ") - " . $e->getMessage() . "\n";
} finally {
    echo "Cleaning up.\n";
    $runner->cleanUp();
}
