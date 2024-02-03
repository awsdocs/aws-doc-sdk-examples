<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[iam.php.remove_user_from_group.complete]
// snippet-start:[iam.php.remove_user_from_group.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.remove_user_from_group.import]

/**
 * Removes the specified user from the specified group.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.remove_user_from_group.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

try {
    $result = $client->removeUserFromGroup([
        // GroupName is required
        'GroupName' => 'string',
        // UserName is required
        'UserName' => 'string',
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[iam.php.remove_user_from_group.main]
// snippet-end:[iam.php.remove_user_from_group.complete]
