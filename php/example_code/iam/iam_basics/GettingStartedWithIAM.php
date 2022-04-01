<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 *
 * Shows how to use AWS SDK for PHP v3 to get started using AWS Identity and Access Management (IAM).
 * Create an IAM user, assume a role, and perform AWS actions.
 *
 * 1. Create a user that has no permissions.
 * 2. Create a role and policy that grant s3:ListAllMyBuckets permission.
 * 3. Grant the user permission to assume the role.
 * 4. Create an S3 client object as the user and try to list buckets (this should fail).
 * 5. Get temporary credentials by assuming the role.
 * 6. Create an S3 client object with the temporary credentials and list the buckets (this should succeed).
 * 7. Delete all the resources.
 *
 * You will need to run the following command to install dependencies:
 * composer install
 *
 * Then run the example either directly with:
 * php GettingStartedWithIAM.php
 *
 * or as a PHPUnit test:
 * vendor/bin/phpunit IAMBasicsTests.php
 *
 **/

# snippet-start:[php.example_code.iam.iam_basics.scenario]
namespace Iam\Basics;

require 'vendor/autoload.php';

use Aws\Credentials\Credentials;
use Aws\S3\Exception\S3Exception;
use Aws\S3\S3Client;
use Aws\Sts\StsClient;
use Iam\IamService;

echo("--------------------------------------\n");
print("Welcome to the Amazon IAM getting started demo using PHP!\n");
echo("--------------------------------------\n");
# snippet-start:[php.example_code.iam.iam_basics.startService]
$uuid = uniqid();
$service = new IamService();
# snippet-end:[php.example_code.iam.iam_basics.startService]

# snippet-start:[php.example_code.iam.iam_basics.createUser]
$user = $service->createUser("iam_demo_user_$uuid");
echo "Created user with the arn: {$user['Arn']}\n";
# snippet-end:[php.example_code.iam.iam_basics.createUser]

$key = $service->createAccessKey($user['UserName']);
# snippet-start:[php.example_code.iam.iam_basics.createRole]
$assumeRolePolicyDocument = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Principal\": {\"AWS\": \"{$user['Arn']}\"},
                    \"Action\": \"sts:AssumeRole\"
                }]
            }";
$assumeRoleRole = $service->createRole("iam_demo_role_$uuid", $assumeRolePolicyDocument);
echo "Created role: {$assumeRoleRole['RoleName']}\n";
# snippet-end:[php.example_code.iam.iam_basics.createRole]

# snippet-start:[php.example_code.iam.iam_basics.createPolicy]
$listAllBucketsPolicyDocument = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Action\": \"s3:ListAllMyBuckets\",
                    \"Resource\": \"arn:aws:s3:::*\"}]
}";
$listAllBucketsPolicy = $service->createPolicy("iam_demo_policy_$uuid", $listAllBucketsPolicyDocument);
echo "Created policy: {$listAllBucketsPolicy['PolicyName']}\n";
# snippet-end:[php.example_code.iam.iam_basics.createPolicy]

# snippet-start:[php.example_code.iam.iam_basics.attachRolePolicy]
$service->attachRolePolicy($assumeRoleRole['RoleName'], $listAllBucketsPolicy['Arn']);
# snippet-end:[php.example_code.iam.iam_basics.attachRolePolicy]

$inlinePolicyDocument = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Action\": \"sts:AssumeRole\",
                    \"Resource\": \"{$assumeRoleRole['Arn']}\"}]
}";
$inlinePolicy = $service->createUserPolicy("iam_demo_inline_policy_$uuid", $inlinePolicyDocument, $user['UserName']);
//First, fail to list the buckets with the user
$credentials = new Credentials($key['AccessKeyId'], $key['SecretAccessKey']);
$s3Client = new S3Client(['region' => 'us-west-2', 'version' => 'latest', 'credentials' => $credentials]);
try {
    $s3Client->listBuckets([
    ]);
    echo "this should not run";
} catch (S3Exception $exception) {
    echo "successfully failed!\n";
}

$stsClient = new StsClient(['region' => 'us-west-2', 'version' => 'latest', 'credentials' => $credentials]);
sleep(10);
$assumedRole = $stsClient->assumeRole([
    'RoleArn' => $assumeRoleRole['Arn'],
    'RoleSessionName' => "DemoAssumeRoleSession_$uuid",
]);
$assumedCredentials = [
    'key' => $assumedRole['Credentials']['AccessKeyId'],
    'secret' => $assumedRole['Credentials']['SecretAccessKey'],
    'token' => $assumedRole['Credentials']['SessionToken'],
];
$s3Client = new S3Client(['region' => 'us-west-2', 'version' => 'latest', 'credentials' => $assumedCredentials]);
try {
    $s3Client->listBuckets([
    ]);
    echo "this should now run!\n";
} catch (S3Exception $exception) {
    echo "this should now not fail\n";
}

$service->detachRolePolicy($assumeRoleRole['RoleName'], $listAllBucketsPolicy['Arn']);
$deletePolicy = $service->deletePolicy($listAllBucketsPolicy['Arn']);
echo "Delete policy: {$listAllBucketsPolicy['PolicyName']}\n";
$deletedRole = $service->deleteRole($assumeRoleRole['Arn']);
echo "Deleted role: {$assumeRoleRole['RoleName']}\n";
$deletedKey = $service->deleteAccessKey($key['AccessKeyId']);
$deletedUser = $service->deleteUser($user['UserName']);
echo "Delete user: {$user['UserName']}\n";

# snippet-end:[php.example_code.iam.iam_basics.scenario]
