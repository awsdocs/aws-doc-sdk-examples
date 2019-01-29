<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 *
 *
 */
// snippet-start:[lightsail.php.create_instance.complete]
// snippet-start:[lightsail.php.create_instance.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Lightsail\LightsailClient;

// snippet-end:[lightsail.php.create_instance.import]

/**
 * Create an Amazon Lightsail Instance.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Lightsail Client
// snippet-start:[lightsail.php.create_instance.main] 
$client = new Aws\Lightsail\LightsailClient([
    'profile' => 'default',
    'version' => '2016-11-28',
    'region' => 'us-east-2'
]);

$availabilityZone = 'us-east-2a';
$blueprintId = 'amazon_linux_2018_03_0_2';
$bundleId = 'nano_2_0';
$instanceName = 'AWS_SDK_PHP-Amazon_Linux';


try {
    $result = $client->createInstances([
        'availabilityZone' => $availabilityZone,
        'blueprintId' => $blueprintId,
        'bundleId' => $bundleId,
        'instanceNames' => [$instanceName],
        'tags' => [
            [
                'key' => 'SDK',
                'value' => 'Made with AWS SDK for PHP',
            ],
            [
                'key' => 'Type',
                'value' => 'Amazon Linux',
            ]
        ],
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[lightsail.php.create_instance.main]
// snippet-end:[lightsail.php.create_instance.complete] 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateInstances.php demonstrates how to create an Amazon Lightsail Instance.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[createInstances]
// snippet-keyword:[Amazon Lightsail]
// snippet-service:[lightsail]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-1-28]
// snippet-sourceauthor:[jschwarzwalder (AWS)]