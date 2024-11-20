<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3;

use Aws\Exception\AwsException;
use AwsUtilities\RunnableExample;
use S3\express\S3ExpressBasics;

require_once __DIR__ . "/vendor/autoload.php";

require "S3ExpressBasics.php";

try {
/**
 * @var RunnableExample $runner
*/
    $runner = new S3ExpressBasics();
    $runner->helloService();
    $runner->runExample();
} catch (AwsException $error) {
    echo "Errored with the following: (" . $error->getCode() . ") - " . $error->getMessage();
} finally {
    echo "Cleaning up.\n";
    $runner->cleanUp();
}
