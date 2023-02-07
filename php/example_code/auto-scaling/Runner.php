<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use AutoScaling\GettingStartedWithAutoScaling;
use AwsUtilities\RunnableExample;

include __DIR__ . "/vendor/autoload.php";

include "GettingStartedWithAutoScaling.php";

try {
    /** @var RunnableExample $runner */
    $runner = new GettingStartedWithAutoScaling();
    $runner->helloService();
    $runner->runExample();
} catch (Exception $error) {
    echo "Errored with the following: (" . $error->getCode() . ") - " . $error->getMessage();
} finally {
    echo "Cleaning up.\n";
    $runner->cleanUp();
}
