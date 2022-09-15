<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AwsUtilities;

use Aws\Exception\AwsException;

/**
 * You can use this class when an AWS waiter class is not available. Call this function instead of the AWS function
 * call, then pass in the function that you want to wait on. You can change the wait time and max attempts by using the
 * static properties. This function catches any AwsException that's thrown, so it might erroneously wait if there is a
 * problem with your API call. Turn on the $verbose flag during development and testing to see details about the root
 * problem.
 */
abstract class AWSServiceClass
{
    public static int $maxWaitAttempts = 10;
    public static int $waitTime = 2;

    public function customWaiter($function, $verbose = false)
    {
        $attempts = 1;
        $hasFinished = false;
        $result = false;
        while (!$hasFinished) {
            try {
                $result = $function();
                $hasFinished = true;
            } catch (AwsException $exception) {
                if ($verbose) {
                    echo "Attempt failed because of: {$exception->getMessage()}.\n";
                    echo "Waiting " . static::$waitTime . " seconds before trying again.\n";
                    echo (static::$maxWaitAttempts - $attempts) . " attempts left.\n";
                }
                ++$attempts;
                if ($attempts > static::$maxWaitAttempts) {
                    throw $exception;
                }
                sleep(static::$waitTime);
            }
        }
        return $result;
    }
}
