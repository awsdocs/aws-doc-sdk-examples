<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ec2-examples-using-elastic-ip-addresses.html
 *
 *
 *
 */

// snippet-start:[ec2.php.describe_instances.complete]
// snippet-start:[ec2.php.describe_instances.import]

require 'vendor/autoload.php';

use Aws\Ec2\Ec2Client;

// snippet-end:[ec2.php.describe_instances.import]
/**
 * Describe Instances
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.describe_instances.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);
$result = $ec2Client->describeInstances();
echo "Instances: \n";
foreach ($result['Reservations'] as $reservation) {
    foreach ($reservation['Instances'] as $instance) {
        echo "InstanceId: {$instance['InstanceId']} - {$instance['State']['Name']} \n";
    }
}

// snippet-end:[ec2.php.describe_instances.main]
// snippet-end:[ec2.php.describe_instances.complete]
