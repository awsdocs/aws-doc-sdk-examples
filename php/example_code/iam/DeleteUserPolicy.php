<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[iam.php.delete_user_policy.complete]
// snippet-start:[iam.php.delete_user_policy.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.delete_user_policy.import]

/**
 * Delete user policy
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.delete_user_policy.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

try {
    $result = $client->deleteUserPolicy([
        // UserName is required
        'UserName' => 'string',
        // PolicyName is required
        'PolicyName' => 'string',
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[iam.php.delete_user_policy.main]
// snippet-end:[iam.php.delete_user_policy.complete]
