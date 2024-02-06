<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[mediaconvert.php.create_client.complete]
// snippet-start:[mediaconvert.php.create_client.import]
require 'vendor/autoload.php';

// snippet-end:[mediaconvert.php.create_client.import]
/**
 * Creating an Amazon Elemental MediaConvert Client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a MediaConvert Client
// snippet-start:[mediaconvert.php.create_client.main]
$client = new Aws\MediaConvert\MediaConvertClient([
    'profile' => 'default',
    'version' => '2017-08-29',
    'region' => 'us-east-2'
]);

var_dump($client);

// snippet-end:[mediaconvert.php.create_client.main]
// snippet-end:[mediaconvert.php.create_client.complete]
