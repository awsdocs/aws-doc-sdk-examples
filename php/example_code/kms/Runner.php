<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace KMS;

use Aws\Exception\AwsException;
use AwsUtilities\RunnableExample;

require __DIR__ . "/vendor/autoload.php";

require "KmsBasics.php";

try {
    /**
 * @var RunnableExample $runner
*/
    $runner = new KmsBasics();
    $runner->helloService();
    $runner->runExample();
} catch (AwsException $error) {
    echo "Errored with the following: (" . $error->getCode() . ") - " . $error->getMessage();
} finally {
    echo "Cleaning up.\n";
    $runner->cleanUp();
}
