<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-certs.html
 *
 *
 *
 */
// snippet-start:[iam.php.update_server_certificate.complete]
// snippet-start:[iam.php.update_server_certificate.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.update_server_certificate.import]

/**
 * Update server certificate
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.update_server_certificate.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

try {
    $result = $client->updateServerCertificate([
        // ServerCertificateName is required
        'ServerCertificateName' => 'string',
        'NewServerCertificateName' => 'string',
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[iam.php.update_server_certificate.main]
// snippet-end:[iam.php.update_server_certificate.complete]
