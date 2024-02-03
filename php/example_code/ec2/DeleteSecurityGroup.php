<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ec2-examples-using-security-groups.html
 *
 *
 *
 */
// snippet-start:[ec2.php.delete_security_group.complete]
// snippet-start:[ec2.php.delete_security_group.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.delete_security_group.import]
/**
 * Delete a Security Group
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.delete_security_group.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$securityGroupId = 'my-security-group-id';

$result = $ec2Client->deleteSecurityGroup([
    'GroupId' => $securityGroupId
]);

var_dump($result);

// snippet-end:[ec2.php.delete_security_group.main]
// snippet-end:[ec2.php.delete_security_group.complete]
