<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.private_distribution.complete]
// snippet-start:[cloudfront.php.private_distribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient;
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.private_distribution.import]

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
 * Get a Signed URL for an Amazon CloudFront Distribution.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[cloudfront.php.private_distribution.main]
function signPrivateDistribution($cloudFrontClient)
{

}

function signAPrivateDistribution()
{

}

// Uncomment the following line to run this code in an AWS account.
signAPrivateDistribution();

$client = new CloudFrontClient([
    'profile' => 'default',
    'version' => '2014-11-06',
    'region' => 'us-east-1'
]);

$resourceKey = 'rtmp://example-distribution.cloudfront.net/videos/example.mp4';
$expires = time() + 300;

$signedUrlCannedPolicy = $client->getSignedUrl([
    'url' => $resourceKey,
    'expires' => $expires,
    'private_key' => '/path/to/your/cloudfront-private-key.pem',
    'key_pair_id' => '<CloudFront key pair id>'
]);

// snippet-end:[cloudfront.php.private_distribution.main]
// snippet-end:[cloudfront.php.private_distribution.complete] 
// snippet-sourcedescription:[SignPriveDistribution.php demonstrates how to provide users access to your private content using an Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getSignedUrl]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-20]
// snippet-sourceauthor:[pccornel (AWS)]
