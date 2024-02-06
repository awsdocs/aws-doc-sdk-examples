<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ec2-examples-working-with-key-pairs.html
 *
 *
 *
 */
// snippet-start:[ec2.php.describe_key_pairs.complete]
// snippet-start:[ec2.php.describe_key_pairs.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.describe_key_pairs.import]
/**
 * Describe KeyPairs
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.describe_key_pairs.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$result = $ec2Client->describeKeyPairs();

var_dump($result);

// snippet-end:[ec2.php.describe_key_pairs.main]
// snippet-end:[ec2.php.describe_key_pairs.complete]
