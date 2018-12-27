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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the
 *
 */

require 'vendor/autoload.php';

use Aws\Kinesis\KinesisClient;
use Aws\Exception\AwsException;

/**
 * Remove an existing Amazon Kinesis Data Stream.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KinesisClient
$kinesisClient = new Aws\Kinesis\KinesisClient([
    'profile' => 'default',
    'version' => '2013-12-02',
    'region' => 'us-east-2'
]);

$name = "my_stream_name";

try {
    $result = $kinesisClient->deleteStream([
        'StreamName' => $name,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
 

//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[DeleteDataStream.php demonstrates how to delete a Amazon Kinesis Data Streams stream.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Kinesis]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-27]
//snippet-sourceauthor:[jschwarzwalder (AWS)]

