<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AwsUtilities;

use Aws\Exception\AwsException;
use Iam\IAMService;

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

    /**
     * This is a very common task for all services, so this is a helper function to use everywhere.
     */

    /**
     * @param $roleName string the name to give the role
     * @param string|null $rolePolicyDocument
     * @param $clientArgs array|null optional array of client arguments
     * @param $verbose bool whether to echo debug information
     * @return string the created role's ARN
     */
    public function createRole(
        string $roleName,
        string $rolePolicyDocument = null,
        array $clientArgs = null,
        bool $verbose = false
    ): string {
        $clientArgs = $clientArgs ?: [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $iamService = new IAMService($clientArgs);

        $rolePolicyDocument = $rolePolicyDocument ?: "{
            \"Version\": \"2012-10-17\",
            \"Statement\": [
                {
                    \"Effect\": \"Allow\",
                    \"Principal\": {
                        \"Service\": \"states.amazonaws.com\"
                    },
                    \"Action\": \"sts:AssumeRole\"
                }
            ]
        }";

        $role = $iamService->createRole($roleName, $rolePolicyDocument);
        echo $verbose ? "Created role {$role['RoleName']}.\n" : "";
        $iamService->attachRolePolicy(
            $role['RoleName'],
            "arn:aws:iam::aws:policy/AWSStepFunctionsFullAccess"
        );
        echo $verbose ? "Attached the AWSStepFunctionsFullAccess to {$role['RoleName']}.\n" : "";
        return $role['Arn'];
    }

    /**
     * The counterpart to create role.
     * @param $roleArn
     * @param $clientArgs
     * @return void
     */
    public function deleteRole($roleArn, $clientArgs = null)
    {
        $clientArgs = $clientArgs ?: [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $iamService = new IAMService($clientArgs);
        $iamService->deleteRole($roleArn);
    }
}
