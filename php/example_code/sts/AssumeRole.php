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
 */
require 'vendor/autoload.php';

use Aws\Sts\StsClient;
use Aws\Exception\AwsException;

/**
 * Assume Role
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

$client = new StsClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => 'latest'
]);

$roleToAssumeArn = 'arn:aws:iam::123456789012:role/RoleName';

try {
    $result = $client->assumeRole([
        'RoleArn' => $roleToAssumeArn,
        'RoleSessionName' => 'session1'
    ]);
    // output AssumedRole credentials, you can use these credentials
    // to initiate a new AWS Service client with the IAM Role's permissions
    var_dump($result[Credentials]);
} catch (AwsException $e) {
    // output error message if fails
    error_log($e->getMessage());
}
 

//snippet-sourcedescription:[AssumeRole demonstrates how to how to retrieve an assumed role that you can use for cross-account or federation access to an AWS resource.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Security Token Service (STS)]
//snippet-service:[sts]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-08]
//snippet-sourceauthor:[jschwarzwalder]

