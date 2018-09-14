<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *  ABOUT THIS PHP SAMPLE: This sample demonstrates how to retrieve a secreat from
 * from Secrets manager. It is accessible from the Secrets Manager Console.
 *
 */

require 'vendor/autoload.php';

use Aws\SecretsManager\SecretsManagerClient;
use Aws\Exception\AwsException;

/**
 * In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
 * See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
 * We rethrow the exception by default.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Secrets Manager Client
$client = new SecretsManagerClient([
    'profile' => 'default',
    'version' => '2017-10-17',
    'region' => 'us-east-1',
]);

$secret_name = 'SecretName';

try {
    $result = $client->getSecretValue([
        'SecretId' => $secret_name,
    ]);
    print("Retrieved secret: " . $result['Name']);
} catch (AwsException $e) {
    //echo $e->getMessage();
    //echo "\n";

    $error = $e->getAwsErrorCode();
    if ($error == 'DecryptionFailureException') {
        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw $e;
    } elseif ($error == 'InternalServiceErrorException') {
        // An error occurred on the server side.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw $e;

    } elseif ($error == 'InvalidParameterException') {
        // You provided an invalid value for a parameter.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw $e;
    } elseif ($error == 'InvalidRequestException') {
        // You provided a parameter value that is not valid for the current state of the resource.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw $e;
    } elseif ($error == 'ResourceNotFoundException') {
        # We can't find the resource that you asked for.
        # Deal with the exception here, and/or rethrow at your discretion.
        throw $e;
    } else {
        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if ($result['SecretString']) {
            $secret = $result['SecretString'];
        } else {
            $decoded_secret = base64_decode($result['SecretBinary']);
        }
    }


}