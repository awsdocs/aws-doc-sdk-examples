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
 * Purpose: Updates information about an Amazon CloudFront distribution.
 *
 * Prerequisites: An existing CloudFront distribution.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized CloudFront client.
 * - $distributionId: The ID of the distribution to update information about.
 * - $distributionConfig: A collection of settings for the distribution. 
 *   This value comes from the companion getDistributionConfig function.
 * - $eTag: The ETag header value for the distribution. This value comes from
 *   the companion getDistributionETag function.
 * 
 * Returns: Information about the updated distribution; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.updatedistribution.main]
function updateDistribution($cloudFrontClient, $distributionId, 
    $distributionConfig, $eTag)
{
    try {
        $result = $cloudFrontClient->updateDistribution([
            'DistributionConfig' => $distributionConfig,
            'Id' => $distributionId,
            'IfMatch' => $eTag
        ]);

        return 'The distribution with the following effective URI has ' .
            'been updated: ' . $result['@metadata']['effectiveUri'];

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function getDistributionConfig($cloudFrontClient, $distributionId)
{
    try {
        $result = $cloudFrontClient->getDistribution([
            'Id' => $distributionId,
        ]);

        if (isset($result['Distribution']['DistributionConfig']))
        {
            return [
                'DistributionConfig' => $result['Distribution']['DistributionConfig'],
                'effectiveUri' => $result['@metadata']['effectiveUri']
            ];
        } else {
            return [
                'Error' => 'Error: Cannot find distribution configuration details.',
                'effectiveUri' => $result['@metadata']['effectiveUri']
            ];
        }

    } catch (AwsException $e) {
        return [
            'Error' => 'Error: ' . $e->getAwsErrorMessage()
        ];
    }
}

function getDistributionETag($cloudFrontClient, $distributionId)
{

    try {
        $result = $cloudFrontClient->getDistribution([
            'Id' => $distributionId,
        ]);
        
        if (isset($result['ETag']))
        {
            return [
                'ETag' => $result['ETag'],
                'effectiveUri' => $result['@metadata']['effectiveUri']
            ]; 
        } else {
            return [
                'Error' => 'Error: Cannot find distribution ETag header value.',
                'effectiveUri' => $result['@metadata']['effectiveUri']
            ];
        }

    } catch (AwsException $e) {
        return [
            'Error' => 'Error: ' . $e->getAwsErrorMessage()
        ];
    }
}

function updateADistribution()
{
    // $distributionId = 'E1BTGP2EXAMPLE';
    $distributionId = 'E1X3BKQ569KEMH';

    $cloudFrontClient = new CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);

    // To change a distribution, you must first get the distribution's 
    // ETag header value.
    $eTag = getDistributionETag($cloudFrontClient, $distributionId);

    if (array_key_exists('Error', $eTag)) {
        exit($eTag['Error']);
    }

    // To change a distribution, you must also first get information about 
    // the distribution's current configuration. Then you must use that 
    // information to build a new configuration.
    $currentConfig = getDistributionConfig($cloudFrontClient, $distributionId);

    if (array_key_exists('Error', $currentConfig)) {
        exit($currentConfig['Error']);
    }

    // To change a distribution's configuration, you can set the 
    // distribution's related configuration value as part of a change request, 
    // for example:
    // 'Enabled' => true
    // Some configuration values are required to be specified as part of a change 
    // request, even if you don't plan to change their values. For ones you 
    // don't want to change but are required to be specified, you can just reuse 
    // their current values, as follows. 
    $distributionConfig = [
        'CallerReference' => $currentConfig['DistributionConfig']["CallerReference"], 
        'Comment' => $currentConfig['DistributionConfig']["Comment"], 
        'DefaultCacheBehavior' => $currentConfig['DistributionConfig']["DefaultCacheBehavior"], 
        'DefaultRootObject' => $currentConfig['DistributionConfig']["DefaultRootObject"],
        'Enabled' => $currentConfig['DistributionConfig']["Enabled"], 
        'Origins' => $currentConfig['DistributionConfig']["Origins"], 
        'Aliases' => $currentConfig['DistributionConfig']["Aliases"],
        'CustomErrorResponses' => $currentConfig['DistributionConfig']["CustomErrorResponses"],
        'HttpVersion' => $currentConfig['DistributionConfig']["HttpVersion"],
        'CacheBehaviors' => $currentConfig['DistributionConfig']["CacheBehaviors"],
        'Logging' => $currentConfig['DistributionConfig']["Logging"],
        'PriceClass' => $currentConfig['DistributionConfig']["PriceClass"],
        'Restrictions' => $currentConfig['DistributionConfig']["Restrictions"],
        'ViewerCertificate' => $currentConfig['DistributionConfig']["ViewerCertificate"],
        'WebACLId' => $currentConfig['DistributionConfig']["WebACLId"]
    ];

    echo updateDistribution($cloudFrontClient, $distributionId,
        $distributionConfig, $eTag['ETag']);
}

// Uncomment the following line to run this code in an AWS account.
// updateADistribution();
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