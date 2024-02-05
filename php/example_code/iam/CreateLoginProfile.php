<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[iam.php.create_login_profile.complete]
// snippet-start:[iam.php.create_login_profile.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.create_login_profile.import]

/**
 * Creates a password for the specified user, giving the user the ability
 * to access AWS services through the AWS Management Console
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.create_login_profile.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

try {
    $result = $client->createLoginProfile(array(
        // UserName is required
        'UserName' => 'string',
        // Password is required
        'Password' => 'string',
        'PasswordResetRequired' => false,
    ));
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[iam.php.create_login_profile.main]
// snippet-end:[iam.php.create_login_profile.complete]
