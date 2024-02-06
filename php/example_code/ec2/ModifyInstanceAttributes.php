<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ec2.php.modify_instance_attributes.complete]
// snippet-start:[ec2.php.modify_instance_attributes.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.modify_instance_attributes.import]
/**
 * Modify Instance Attributes
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.modify_instance_attributes.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$instanceId = 'InstanceId';

$result = $ec2Client->modifyInstanceAttribute([
    'DryRun' => false,
    // InstanceId is required
    'InstanceId' => $instanceId,
    'Attribute' => 'string',
    'Value' => 'string'
]);

var_dump($result);

// snippet-end:[ec2.php.modify_instance_attributes.main]
// snippet-end:[ec2.php.modify_instance_attributes.complete]
