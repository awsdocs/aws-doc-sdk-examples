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
//snippet-start:[cloudfront.php.getdistribution.complete]
//snippet-start:[cloudfront.php.getdistribution.import]

require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
//snippet-end:[cloudfront.php.getdistribution.import]


/**
 * Retrieve an Amazon CloudFront Distribution.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a CloudFront Client 
//snippet-start:[cloudfront.php.getdistribution.main]
$client = new Aws\CloudFront\CloudFrontClient([
    'profile' => 'default',
    'version' => '2018-06-18',
    'region' => 'us-east-2'
]);

$distributionId = 'E1A2B3C4D5E';

try {
    $result = $client->getDistribution([
        'Id' => $distributionId, //REQUIRED
    ]);
    print("<p>The Distribution " . $result['Distribution']['Id'] . " is currently " . $result['Distribution']['Status'] . "</p>");
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
 
//snippet-end:[cloudfront.php.getdistribution.main]
//snippet-end:[cloudfront.php.getdistribution.complete]
//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[GetDistribution.php demonstrates how to retrieve an Amazon CloudFront Distribution.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[getDistribution]
//snippet-keyword:[Amazon CloudFront]
//snippet-service:[cloudfront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-27]
//snippet-sourceauthor:[jschwarzwalder (AWS)]