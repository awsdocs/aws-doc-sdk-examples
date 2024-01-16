<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*/

// snippet-start:[secretsmanager.php.add_new_secret_value.complete]
// snippet-start:[secretsmanager.php.add_new_secret_value.import]
require 'vendor/autoload.php';
use Aws\SecretsManager\SecretsManagerClient;
use Aws\Exception\AwsException;
// snippet-end:[secretsmanager.php.add_new_secret_value.import]

/**
 * Create a new version of a secret with a new value in an AWS Secret Manager Secret.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Secrets Manager Client
// snippet-start:[secretsmanager.php.add_new_secret_value.main]
$client = new SecretsManagerClient([
    'profile' => 'default',
    'version' => '2017-10-17',
    'region' => 'us-west-2'
]);
$secretName = 'MySecretName';
$secret = json_encode([
    "username" => getenv("SMDEMO_USERNAME"),
    "password" => getenv("SMDEMO_PASSWORD"),
]);
try {
    $result = $client->putSecretValue([
        'SecretId' => $secretName,
        'SecretString' => $secret,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[secretsmanager.php.add_new_secret_value.main]
// snippet-end:[secretsmanager.php.add_new_secret_value.complete]
