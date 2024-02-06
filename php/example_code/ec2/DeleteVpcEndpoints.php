<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ec2.php.delete_vpc_endpoints.complete]
// snippet-start:[ec2.php.delete_vpc_endpoints.import]

require 'vendor/autoload.php';

// snippet-end:[ec2.php.delete_vpc_endpoints.import]
/**
 * Delete VPC Endpoint
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[ec2.php.delete_vpc_endpoints.main]
$ec2Client = new Aws\Ec2\Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$result = $ec2Client->deleteVpcEndpoints([
    // VpcEndpointIds is required
    'VpcEndpointIds' => ['vpce-63da2e0a']
]);

var_dump($result);

// snippet-end:[ec2.php.delete_vpc_endpoints.main]
// snippet-end:[ec2.php.delete_vpc_endpoints.complete]
