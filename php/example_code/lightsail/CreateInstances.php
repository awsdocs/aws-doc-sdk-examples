<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 *
 *
 */
// snippet-start:[lightsail.php.create_instance.complete]
// snippet-start:[lightsail.php.create_instance.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[lightsail.php.create_instance.import]

/**
 * Create an Amazon Lightsail Instance.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Lightsail Client
// snippet-start:[lightsail.php.create_instance.main]
$client = new Aws\Lightsail\LightsailClient([
    'profile' => 'default',
    'version' => '2016-11-28',
    'region' => 'us-east-2'
]);

$availabilityZone = 'us-east-2a';
$blueprintId = 'amazon_linux_2018_03_0_2';
$bundleId = 'nano_2_0';
$instanceName = 'AWS_SDK_PHP-Amazon_Linux';


try {
    $result = $client->createInstances([
        'availabilityZone' => $availabilityZone,
        'blueprintId' => $blueprintId,
        'bundleId' => $bundleId,
        'instanceNames' => [$instanceName],
        'tags' => [
            [
                'key' => 'SDK',
                'value' => 'Made with AWS SDK for PHP',
            ],
            [
                'key' => 'Type',
                'value' => 'Amazon Linux',
            ]
        ],
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[lightsail.php.create_instance.main]
// snippet-end:[lightsail.php.create_instance.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
