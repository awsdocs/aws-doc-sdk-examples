<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 *
 *
 */
// snippet-start:[lightsail.php.get_operations.complete]
// snippet-start:[lightsail.php.get_operations.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[lightsail.php.get_operations.import]

/**
 * Retrieve information about the past 200 operations on Amazon Lightsail Instances.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Lightsail Client
// snippet-start:[lightsail.php.get_operations.main]
$client = new Aws\Lightsail\LightsailClient([
    'profile' => 'default',
    'version' => '2016-11-28',
    'region' => 'us-east-2'
]);

try {
    $result = $client->getOperations();
    if ($result['operations']) {
        foreach ($result['operations'] as $operation) {
            print($operation['operationType'] . " Operation " . $operation['id'] . " on " .
                $operation['resourceType'] . " " . $operation['resourceName'] . ".\n");
        }
    } else {
        print("No operations found.\n");
    }
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[lightsail.php.get_operations.main]
// snippet-end:[lightsail.php.get_operations.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
