<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/emc-examples-jobs.html
 *
 *
 *
 */
// snippet-start:[mediaconvert.php.cancel_job.complete]
// snippet-start:[mediaconvert.php.cancel_job.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\MediaConvert\MediaConvertClient;

// snippet-end:[mediaconvert.php.cancel_job.import]

/**
 * Cancel a Job for AWS Elemental MediaConvert.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an AWSMediaConvert client object with your account specific endpoint.
// snippet-start:[mediaconvert.php.cancel_job.main]
$mediaConvertClient = new MediaConvertClient([
    'version' => '2017-08-29',
    'region' => 'us-east-2',
    'profile' => 'default'
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

// snippet-end:[mediaconvert.php.cancel_job.main]
// snippet-end:[mediaconvert.php.cancel_job.complete]
