<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ec2.php.revoke_security_group_ingress.complete]
// snippet-start:[ec2.php.revoke_security_group_ingress.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.revoke_security_group_ingress.import]
/**
 * Revoke SecurityGroup Ingress
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.revoke_security_group_ingress.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$result = $ec2Client->revokeSecurityGroupIngress([
    'GroupName' => 'string',
    'SourceSecurityGroupName' => 'string'
]);

var_dump($result);

// snippet-end:[ec2.php.revoke_security_group_ingress.main]
// snippet-end:[ec2.php.revoke_security_group_ingress.complete]
