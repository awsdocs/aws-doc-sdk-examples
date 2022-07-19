<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * This is a helper function for command line programs to receive prefab input from a test.
 * To use this function, place the following line at the beginning of your class:
use function AwsUtilities\testable_readline;
 * Alternatively, add the following to your composer.json file:
"files": ["../../aws_utilities/TestableReadline.php"]
 * Then, use testable_readline instead of readline. It will function the same as readline during command line
 * interactions. To automate testing, add values to the global $argv array in the same order that they would be entered
 * in the command line. They will be consumed and passed as the return value instead of requiring a user to manually
 * type the values.
 */

function testable_readline($prompt)
{
    global $argv;
    if (count($argv) > 1) {
        $zero = array_shift($argv);
        $one = array_shift($argv);
        $value = array_shift($argv);
        array_unshift($argv, $one, $zero);
        return $value;
    }
    return readline($prompt);
}
