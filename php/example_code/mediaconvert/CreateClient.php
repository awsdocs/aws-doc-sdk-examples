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
 */

require 'vendor/autoload.php';

use Aws\MediaConvert\MediaConvertClient;

/**
 * Creating an Amazon Elemental MediaConvert Client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a MediaConvert Client
$client = new Aws\MediaConvert\MediaConvertClient([
    'profile' => 'default',
    'version' => '2017-08-29',
    'region' => 'us-east-2'
]);

var_dump($client);
 

//snippet-sourcedescription:[CreateClient.php demonstrates how to create a MediaConvert Client that you can use to manage jobs.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaConvert]
//snippet-service:[mediaconvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-09-20]
//snippet-sourceauthor:[jschwarzwalder]

