<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*/

// snippet-start:[secretsmanager.php.remove_label.complete]
// snippet-start:[secretsmanager.php.remove_label.import]
require 'vendor/autoload.php';

use Aws\SecretsManager\SecretsManagerClient;
use Aws\Exception\AwsException;
// snippet-end:[secretsmanager.php.remove_label.import]

/**
 * Delete a label attached to a version of secret
 *
 * Use ListSecretVersions.php to identify the VersionID for a Secret.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Secrets Manager Client
// snippet-start:[secretsmanager.php.remove_label.main]
$client = new SecretsManagerClient([
    'profile' => 'default',
    'version' => '2017-10-17',
    'region' => 'us-west-2'
]);

$secretName = 'MySecretName';
$version_tag = 'AWSCURRENT';
$version_id = 'EXAMPLE1-90ab-cdef-fedc-ba987SECRET1';

try {
    $result = $client->updateSecretVersionStage([
        'VersionStage' => $version_tag,
        'SecretId' => $secretName,
        'RemoveFromVersionId' => $version_id,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[secretsmanager.php.remove_label.main]
// snippet-end:[secretsmanager.php.remove_label.complete]
