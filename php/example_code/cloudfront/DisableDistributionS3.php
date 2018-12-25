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
 *
 *
 */
//snippet-start:[cloudfront.php.disabledistribution.complete]
//snippet-start:[cloudfront.php.disabledistribution.import]

require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
//snippet-end:[cloudfront.php.disabledistribution.import]


/**
 * Disable an Amazon CloudFront Distribution so that it can be deleted.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a CloudFront Client 
//snippet-start:[cloudfront.php.disabledistribution.main]
$client = new Aws\CloudFront\CloudFrontClient([
    'profile' => 'default',
    'version' => '2018-06-18',
    'region' => 'us-east-2'
]);


$id = 'E1A2B3C4D5E';

try {
    $result = $client->getDistribution([
        'Id' => $id,
    ]);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

$enabled = false;
$currentConfig = $result["Distribution"]["DistributionConfig"];
$ETag = $result["ETag"];

$distribution = [
    'CacheBehaviors' => $currentConfig["CacheBehaviors"], //REQUIRED 
    'CallerReference' => $currentConfig["CallerReference"], // REQUIRED
    'Comment' => $currentConfig["Comment"], // REQUIRED
    'DefaultCacheBehavior' => $currentConfig["DefaultCacheBehavior"], // REQUIRED 
    'DefaultRootObject' => $currentConfig["DefaultRootObject"],
    'Enabled' => $enabled, // REQUIRED
    'Origins' => $currentConfig["Origins"], // REQUIRED 
    'Aliases' => $currentConfig["Aliases"],
    'CustomErrorResponses' => $currentConfig["CustomErrorResponses"],
    'HttpVersion' => $currentConfig["HttpVersion"],
    'IsIPV6Enabled' => $currentConfig["IsIPV6Enabled"],
    'Logging' => $currentConfig["Logging"],
    'PriceClass' => $currentConfig["PriceClass"],
    'Restrictions' => $currentConfig["Restrictions"],
    'ViewerCertificate' => $currentConfig["ViewerCertificate"],
    'WebACLId' => $currentConfig["WebACLId"],
];

//var_dump($distribution);

try {
    $result = $client->updateDistribution([
        'DistributionConfig' => $distribution,
        'Id' => $id,
        'IfMatch' => $ETag
    ]);
    print("<p>For The Distribution " . $result['Distribution']['Id'] . " enabled is set to " . $result['Distribution']['DistributionConfig']['Enabled'] . "</p>");
    var_dump($result['Distribution']['DistributionConfig']['Enabled']);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
 
//snippet-end:[cloudfront.php.disabledistribution.main]
//snippet-end:[cloudfront.php.disabledistribution.complete]
//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[DisableDistribution.php demonstrates how to disable an Amazon CloudFront Distribution so it can be deleted.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[updateDistribution]
//snippet-keyword:[getDistribution]
//snippet-keyword:[Amazon CloudFront]
//snippet-service:[cloudfront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-10-30]
//snippet-sourceauthor:[jschwarzwalder (AWS)]