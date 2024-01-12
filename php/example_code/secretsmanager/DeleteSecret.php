<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*/

// snippet-start:[secretsmanager.php.delete_secret.complete]
// snippet-start:[secretsmanager.php.delete_secret.import]
require 'vendor/autoload.php';

use Aws\SecretsManager\SecretsManagerClient;
use Aws\Exception\AwsException;

// snippet-end:[secretsmanager.php.delete_secret.import]

/**
 * Delete a secret from AWS Secrets Manager.
 *
 * Secret will remain for 30 days before deletion unless specified with 'RecoveryWindowInDays'.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Secrets Manager Client
// snippet-start:[secretsmanager.php.delete_secret.main]
$client = new SecretsManagerClient([
    'profile' => 'default',
    'version' => '2017-10-17',
    'region' => 'us-west-2'
]);

$secretName = 'MySecretName';

try {
    $result = $client->deleteSecret([
        'SecretId' => $secretName,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[secretsmanager.php.delete_secret.main]
// snippet-end:[secretsmanager.php.delete_secret.complete]
