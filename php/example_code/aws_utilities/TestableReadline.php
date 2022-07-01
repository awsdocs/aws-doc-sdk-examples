<?php

/**
 * This is a helper function so command line programs can receive prefab input from a test.
 * To use, place the line
use function AwsUtilities\testable_readline;
 * at the beginning of your class. Then use testable_readline instead of readline. It will function the same during
 * command line interactions as readline. To automate testing, add values to the global $argv array in the order they
 * would be entered in the command line. They will be consumed and passed as the return value instead of requiring a
 * user to manually type the values.
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
