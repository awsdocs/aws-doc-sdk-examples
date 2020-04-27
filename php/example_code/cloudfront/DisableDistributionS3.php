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
 * - $distributionConfig: A collection of settings for the distribution. 
 *   This value comes from the companion getDistributionConfig function.
 * - $eTag: The ETag header value for the distribution. This value comes from
 *   the companion getDistributionETag function.
 *
 * Returns: Information about the disable request; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.disabledistribution.main]
function disableDistribution($cloudFrontClient, $distributionId, 
    $distributionConfig, $eTag)
{
    try {
        $result = $cloudFrontClient->updateDistribution([
            'DistributionConfig' => $distributionConfig,
            'Id' => $distributionId,
            'IfMatch' => $eTag
        ]);
        return 'The distribution with the following effective URI has ' .
            'been disabled: ' . $result['@metadata']['effectiveUri'];
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

function disableADistribution()
{
    $distributionId = 'E1BTGP2EXAMPLE';

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);

    // To disable a distribution, you must first get the distribution's 
    // ETag header value.
    $eTag = getDistributionETag($cloudFrontClient, $distributionId);

    if (array_key_exists('Error', $eTag)) {
        exit($eTag['Error']);
    }

    // To delete a distribution, you must also first get information about 
    // the distribution's current configuration. Then you must use that 
    // information to build a new configuration, including setting the new
    // configuration to disabled.
    $currentConfig = getDistributionConfig($cloudFrontClient, $distributionId);

    if (array_key_exists('Error', $currentConfig)) {
        exit($currentConfig['Error']);
    }

    $distributionConfig = [
        'CacheBehaviors' => $currentConfig['DistributionConfig']["CacheBehaviors"],
        'CallerReference' => $currentConfig['DistributionConfig']["CallerReference"],
        'Comment' => $currentConfig['DistributionConfig']["Comment"],
        'DefaultCacheBehavior' => $currentConfig['DistributionConfig']["DefaultCacheBehavior"],
        'DefaultRootObject' => $currentConfig['DistributionConfig']["DefaultRootObject"],
        'Enabled' => false,
        'Origins' => $currentConfig['DistributionConfig']["Origins"],
        'Aliases' => $currentConfig['DistributionConfig']["Aliases"],
        'CustomErrorResponses' => $currentConfig['DistributionConfig']["CustomErrorResponses"],
        'HttpVersion' => $currentConfig['DistributionConfig']["HttpVersion"],
        'Logging' => $currentConfig['DistributionConfig']["Logging"],
        'PriceClass' => $currentConfig['DistributionConfig']["PriceClass"],
        'Restrictions' => $currentConfig['DistributionConfig']["Restrictions"],
        'ViewerCertificate' => $currentConfig['DistributionConfig']["ViewerCertificate"],
        'WebACLId' => $currentConfig['DistributionConfig']["WebACLId"]
    ];

    echo disableDistribution($cloudFrontClient, $distributionId,
        $distributionConfig, $eTag['ETag']);
}

// Uncomment the following line to run this code in an AWS account.
// disableADistribution();
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
// snippet-sourcedate:[2020-04-24]
// snippet-sourceauthor:[pccornel (AWS)]