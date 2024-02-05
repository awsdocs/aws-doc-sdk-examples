<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the KMS Developer Guide topic at
 *  https://docs.aws.amazon.com/kms/latest/developerguide/programming-key-policies.html
 *
 *
 *
 */
// snippet-start:[kms.php.put_key_policy.complete]
// snippet-start:[kms.php.put_key_policy.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[kms.php.put_key_policy.import]

/**
 * Creating an Amazon KMS client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KmsClient
// snippet-start:[kms.php.put_key_policy.main]
$KmsClient = new Aws\Kms\KmsClient([
    'profile' => 'default',
    'version' => '2014-11-01',
    'region' => 'us-east-2'
]);

$keyId = 'arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab';
$policyName = "default";

try {
    $result = $KmsClient->putKeyPolicy([
        'KeyId' => $keyId,
        'PolicyName' => $policyName,
        'Policy' => '{ 
            "Version": "2012-10-17", 
            "Id": "custom-policy-2016-12-07", 
            "Statement": [ 
                { "Sid": "Enable IAM User Permissions", 
                "Effect": "Allow", 
                "Principal": 
                   { "AWS": "arn:aws:iam::111122223333:user/root" }, 
                "Action": [ "kms:*" ], 
                "Resource": "*" }, 
                { "Sid": "Enable IAM User Permissions", 
                "Effect": "Allow", 
                "Principal":                 
                   { "AWS": "arn:aws:iam::111122223333:user/ExampleUser" }, 
                "Action": [
                    "kms:Encrypt*",
                    "kms:GenerateDataKey*",
                    "kms:Decrypt*",
                    "kms:DescribeKey*",
                    "kms:ReEncrypt*"
                ], 
                "Resource": "*" }                 
            ]            
        } '
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[kms.php.put_key_policy.main]
// snippet-end:[kms.php.put_key_policy.complete]
