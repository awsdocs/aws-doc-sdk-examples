<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-getendpoint.html
 *
 */
require 'vendor/autoload.php';

use Aws\MediaConvert\MediaConvertClient;
use Aws\Exception\AwsException;

/**
 * Get Your Account Specific Endpoint for AWS Elemental MediaConvert.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a MediaConvert
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

$single_endpoint_url = $result['Endpoints'][0]['Url'];

print("Your endpoint is " . $single_endpoint_url);

//Create an AWSMediaConvert client object with the endpoint URL that you retrieved:
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default',
    'endpoint' => $single_endpoint_url
]);
 

//snippet-sourcedescription:[GetEndpoint.php demonstrates how to retrieve a custom endpoint where you send your MediaConvert Job.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaConvert]
//snippet-service:[mediaconvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-09-20]
//snippet-sourceauthor:[jschwarzwalder]

