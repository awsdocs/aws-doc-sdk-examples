<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-getendpoint.html
 *
 *
 *
 */
// snippet-start:[mediaconvert.php.get_endpoint.complete]
// snippet-start:[mediaconvert.php.get_endpoint.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\MediaConvert\MediaConvertClient;

// snippet-end:[mediaconvert.php.get_endpoint.import]

/**
 * Get Your Account Specific Endpoint for AWS Elemental MediaConvert.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a MediaConvert
// snippet-start:[mediaconvert.php.get_endpoint.region]
$client = new Aws\MediaConvert\MediaConvertClient([
    'profile' => 'default',
    'version' => '2017-08-29',
    'region' => 'us-east-2'
]);

//retrieve endpoint
try {
    $result = $client->describeEndpoints([]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[mediaconvert.php.get_endpoint.region]
// snippet-start:[mediaconvert.php.get_endpoint.main]
$single_endpoint_url = $result['Endpoints'][0]['Url'];

print("Your endpoint is " . $single_endpoint_url);

//Create an AWSMediaConvert client object with the endpoint URL that you retrieved:
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default',
    'endpoint' => $single_endpoint_url
]);

// snippet-end:[mediaconvert.php.get_endpoint.main]
// snippet-end:[mediaconvert.php.get_endpoint.complete]
