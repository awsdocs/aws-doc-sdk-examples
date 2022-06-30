<?php

namespace AwsUtilities;

use Aws\Exception\AwsException;

/**
 * This class can be used when an AWS waiter class is not available. Call this function instead of the AWS function
 * call, then pass in the function you wish to wait on. The wait time and max attempts can be changed via the static
 * properties. This function will catch any AwsException thrown, so it might erroneously wait if there is a problem
 * with your API call. Turn on the $verbose flag during development and testing to be sure what the root problem is.
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
                    return false;
                }
                sleep(static::$waitTime);
            }
        }
        return $result;
    }
}
