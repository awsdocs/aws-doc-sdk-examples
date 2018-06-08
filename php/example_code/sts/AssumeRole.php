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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/aws-sdk-php/v3/guide/examples/iam-examples-managing-users.html
 *
 */
require 'vendor/autoload.php';

use Aws\Sts\StsClient;
use Aws\Exception\AwsException;

/**
 * Assume Role
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$client = new StsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => 'latest'
]);

$roleToAssumeArn='arn:aws:iam::123456789012:role/RoleName';

try {
    $result = $client->assumeRole([
                           'RoleArn' => $roleToAssumeArn,
                           'RoleSessionName' => 'session1'
                       ]);
    // output AssumedRole credentials, you can use these credentials
    // to initiate a new AWS Service client with the IAM Role's permissions
    var_dump($result[Credentials);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}

