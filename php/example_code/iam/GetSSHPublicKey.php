<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[iam.php.get_ssh_public_key.complete]
// snippet-start:[iam.php.get_ssh_public_key.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.get_ssh_public_key.import]

/**
 * Retrieves the specified SSH public key, including metadata about the key.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.get_ssh_public_key.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

try {
    $result = $client->getSSHPublicKey([
        'Encoding' => 'SSH', // REQUIRED
        'SSHPublicKeyId' => 'SSH_PUBLIC_KEY_ID', // REQUIRED
        'UserName' => 'IAM_USER_NAME', // REQUIRED
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[iam.php.get_ssh_public_key.main]
// snippet-end:[iam.php.get_ssh_public_key.complete]
