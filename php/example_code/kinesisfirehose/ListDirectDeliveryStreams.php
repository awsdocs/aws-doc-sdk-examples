<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the AWS SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/kinesis-firehose-example-delivery-stream.html
 */
// snippet-start:[firehose.php.list_direct_delivery_stream.complete]
// snippet-start:[firehose.php.list_direct_delivery_stream.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[firehose.php.list_direct_delivery_stream.import]

/**
 * List existing Amazon Kinesis Firehose Delivery Streams that have specified output like AWS S3 or AWS Lambda.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KinesisClient
// snippet-start:[firehose.php.list_direct_delivery_stream.main]
$firehoseClient = new Aws\Firehose\FirehoseClient([
    'profile' => 'default',
    'version' => '2015-08-04',
    'region' => 'us-east-2'
]);

try {
    $result = $firehoseClient->listDeliveryStreams([
        'DeliveryStreamType' => 'DirectPut',
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[firehose.php.list_direct_delivery_stream.main]
// snippet-end:[firehose.php.list_direct_delivery_stream.complete]
