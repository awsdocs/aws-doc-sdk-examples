<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.updatedistribution.complete]
// snippet-start:[cloudfront.php.updatedistribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.updatedistribution.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets information about Amazon CloudFront distribution
 * invalidations.
 *
 * Prerequisites: At least one existing Amazon CloudFront invalidation for the 
 * specified distribution.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * - $distributionId: The ID of the distribution to get invalidation 
 *   information about.
 * 
 * Returns: Information about existing distribution invalidations; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

/**
 * Updating an Amazon CloudFront Distribution.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[cloudfront.php.updatedistribution.main]
function updateDistribution($cloudFrontClient)
{

}

function updateADistribution()
{

}

// Uncomment the following line to run this code in an AWS account.
updateADistribution();

$client = new Aws\CloudFront\CloudFrontClient([
    'profile' => 'default',
    'version' => '2018-06-18',
    'region' => 'us-east-1'
]);


$id = 'E1A2B3C4D5E';

try {
    $result = $client->getDistribution([
        'Id' => $id,
    ]);
} catch (AwsException $e) {
    echo $e->getMessage();
    echo "\n";
}

$currentConfig = $result["Distribution"]["DistributionConfig"];
$ETag = $result["ETag"];

$distribution = [
    'CallerReference' => $currentConfig["CallerReference"], // REQUIRED
    'Comment' => $currentConfig["Comment"], // REQUIRED
    'DefaultCacheBehavior' => $currentConfig["DefaultCacheBehavior"], // REQUIRED
    'DefaultRootObject' => $currentConfig["DefaultRootObject"],
    'Enabled' => $currentConfig["Enabled"], // REQUIRED
    'Origins' => $currentConfig["Origins"], // REQUIRED
    'Aliases' => $currentConfig["Aliases"],
    'CustomErrorResponses' => $currentConfig["CustomErrorResponses"],
    'HttpVersion' => $currentConfig["HttpVersion"],
    'CacheBehaviors' => $currentConfig["CacheBehaviors"],
    'Logging' => $currentConfig["Logging"],
    'PriceClass' => $currentConfig["PriceClass"],
    'Restrictions' => $currentConfig["Restrictions"],
    'ViewerCertificate' => $currentConfig["ViewerCertificate"],
    'WebACLId' => $currentConfig["WebACLId"],
];


try {
    $result = $client->updateDistribution([
        'DistributionConfig' => $distribution,
        'Id' => $id,
        'IfMatch' => $ETag
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
 
// snippet-end:[cloudfront.php.updatedistribution.main]
// snippet-end:[cloudfront.php.updatedistribution.complete]
// snippet-sourcedescription:[UpdateDistribution.php demonstrates how to get an Amazon CloudFront distribution and change any of the configurations. To make changes, replace the current configuration value with a new value.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[updateDistribution]
// snippet-keyword:[getDistribution]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-18]
// snippet-sourceauthor:[pccornel (AWS)]