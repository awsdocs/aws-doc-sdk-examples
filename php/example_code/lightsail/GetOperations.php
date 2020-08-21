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
// snippet-start:[lightsail.php.get_operations.complete]
// snippet-start:[lightsail.php.get_operations.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Lightsail\LightsailClient;

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
    $result = $client->getOperations([]);
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
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetOperations.php demonstrates how to retrieve information about the past 200 Amazon Lightsail instance operations.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getOperations]
// snippet-keyword:[Amazon Lightsail]
// snippet-service:[lightsail]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-1-28]
// snippet-sourceauthor:[jschwarzwalder (AWS)]