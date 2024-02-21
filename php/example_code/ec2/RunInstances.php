<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ec2.php.run_instance.complete]
// snippet-start:[ec2.php.run_instance.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.run_instance.import]
/**
 * Run Instances
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.run_instance.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$result = $ec2Client->runInstances([
    'DryRun' => false,
    // ImageId is required
    'ImageId' => 'string',
    // MinCount is required
    'MinCount' => integer,
    // MaxCount is required
    'MaxCount' => integer,
]);

var_dump($result);

// snippet-end:[ec2.php.run_instance.main]
// snippet-end:[ec2.php.run_instance.complete]
