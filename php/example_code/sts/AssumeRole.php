<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sts.php.assume_role.complete]
// snippet-start:[sts.php.assume_role.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\S3\S3Client;
use Aws\Sts\StsClient;

// snippet-end:[sts.php.assume_role.import]

/**
 * Assume Role
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[sts.php.assume_role.main]
$client = new StsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2011-06-15'
]);

$roleToAssumeArn = 'arn:aws:iam::123456789012:role/RoleName';

try {
    $result = $client->assumeRole([
        'RoleArn' => $roleToAssumeArn,
        'RoleSessionName' => 'session1'
    ]);
    // output AssumedRole credentials, you can use these credentials
    // to initiate a new AWS Service client with the IAM Role's permissions

    $s3Client = new S3Client([
        'version'     => '2006-03-01',
        'region'      => 'us-west-2',
        'credentials' =>  [
            'key'    => $result['Credentials']['AccessKeyId'],
            'secret' => $result['Credentials']['SecretAccessKey'],
            'token'  => $result['Credentials']['SessionToken']
        ]
    ]);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

// snippet-end:[sts.php.assume_role.main]
// snippet-end:[sts.php.assume_role.complete]
