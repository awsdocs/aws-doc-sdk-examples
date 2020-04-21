<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.disabledistribution.complete]
// snippet-start:[cloudfront.php.disabledistribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.disabledistribution.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Disables an Amazon CloudFront distribution.
 *
 * Prerequisites: An existing Amazon CloudFront distribution.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * - $distributionId: The distribution's ID.
 * - $distributionConfig: ... This value comes from the companion 
 *   getDisributionConfig function.
 * - $eTag: The ETag header value for the distribution. This value comes from
 *   the companion getDistributionETag function.
 *
 * Returns: Information about the deletion request; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.disabledistribution.main]
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
 
// snippet-end:[cloudfront.php.disabledistribution.main]
// snippet-end:[cloudfront.php.disabledistribution.complete]
// snippet-sourcedescription:[DisableDistributionS3.php demonstrates how to disable an Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[updateDistribution]
// snippet-keyword:[getDistribution]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-27]
// snippet-sourceauthor:[jschwarzwalder (AWS)]