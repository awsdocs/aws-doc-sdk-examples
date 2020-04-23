<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.deletedistribution.complete]
// snippet-start:[cloudfront.php.deletedistribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.deletedistribution.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Deletes an Amazon CloudFront distribution.
 *
 * Prerequisites: An existing Amazon CloudFront distribution. The 
 * distribution must be disabled first.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * - $distributionId: The distribution's ID.
 * - $eTag: The ETag header value for the distribution. This value comes from
 *   the companion getDistributionETag function.
 *
 * Returns: Information about the deletion request; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.deletedistribution.main]
function deleteDistribution($cloudFrontClient, $distributionId, $eTag)
{
    try {
        $result = $cloudFrontClient->deleteDistribution([
            'Id' => $distributionId,
            'IfMatch' => $eTag
        ]);
        return 'The distribution at the following effective URI has ' . 
            'been deleted: ' . $result['@metadata']['effectiveUri'];
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
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
            return $result['ETag'];
        } else {
            return 'Error: Cannot find distribution ETag header value.';
        }

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function deleteADistribution()
{
    $distributionId = 'E17G7YNEXAMPLE';

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);

    // To delete a distribution, you must first get the distribution's 
    // ETag header value.
    $eTag = getDistributionETag($cloudFrontClient, $distributionId);

    if (strpos($eTag, 'Error:') === false) {
        echo deleteDistribution($cloudFrontClient, $distributionId, $eTag);
    } else {
        exit($eTag);
    }
}

// Uncomment the following line to run this code in an AWS account.
// deleteADistribution();
// snippet-end:[cloudfront.php.deletedistribution.main]
// snippet-end:[cloudfront.php.deletedistribution.complete]
// snippet-sourcedescription:[DeleteDistribution.php demonstrates how to delete an Amazon CloudFront distribution that has been disabled.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[deleteDistribution]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-21]
// snippet-sourceauthor:[pccornel (AWS)]