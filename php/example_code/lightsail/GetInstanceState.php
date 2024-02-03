<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 *
 *
 */
// snippet-start:[lightsail.php.get_instance_state.complete]
// snippet-start:[lightsail.php.get_instance_state.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[lightsail.php.get_instance_state.import]

/**
 * Get the state of a single Amazon Lightsail Instance.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Lightsail Client
// snippet-start:[lightsail.php.get_instance_state.main]
$client = new Aws\Lightsail\LightsailClient([
    'profile' => 'default',
    'version' => '2016-11-28',
    'region' => 'us-east-2'
]);

$instanceName = 'AWS_SDK_PHP-Amazon_Linux';

try {
    $result = $client->getInstanceState([
        'instanceName' => $instanceName,
    ]);
    print("Status Code: " . $result['@metadata']['statusCode'] . ", "
        . $instanceName . " is " . $result['state']['name'] . ".\n");
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[lightsail.php.get_instance_state.main]
// snippet-end:[lightsail.php.get_instance_state.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
