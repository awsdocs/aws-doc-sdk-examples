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
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-jobs.html
 *
 */
require 'vendor/autoload.php';

use Aws\MediaConvert\MediaConvertClient;
use Aws\Exception\AwsException;

/**
 * Cancel a Job for AWS Elemental MediaConvert.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an AWSMediaConvert client object with your account specific endpoint.
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default',
    'endpoint' => 'ACCOUNT_ENDPOINT'
]);

try {
    $result = $mediaConvertClient->cancelJob([
        'Id' => 'JOB_ID', // REQUIRED The Job ID of the job to be cancelled.
    ]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
 

//snippet-sourcedescription:[CancelJob.php demonstrates how to cancel an existing MediaConvert Job.]
//snippet-keyword:[PHP]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]

