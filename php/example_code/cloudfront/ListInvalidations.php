<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.listinvalidation.complete]
// snippet-start:[cloudfront.php.listinvalidation.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.listinvalidation.import]

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

// snippet-start:[cloudfront.php.listinvalidation.main]
function listInvalidations($cloudFrontClient, $distributionId)
{
    try {
        $result = $cloudFrontClient->listInvalidations([
            'DistributionId' => $distributionId
        ]);
        return $result;
    } catch (AwsException $e) {
        exit('Error: ' . $e->getAwsErrorMessage());
    }
}

function listTheInvalidations()
{
    $distributionId = 'E1WICG1EXAMPLE';

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);

    $invalidations = listInvalidations($cloudFrontClient, 
        $distributionId);

    if (isset($invalidations['InvalidationList']))
    {
        if ($invalidations['InvalidationList']['Quantity'] > 0)
        {
            foreach ($invalidations['InvalidationList']['Items'] as $invalidation)
            {
                echo 'The invalidation with the ID of ' . $invalidation['Id'] . 
                    ' has the status of ' . $invalidation['Status'] . '.' . "\n";
            }
        } else {
            echo 'Could not find any invalidations for the specified distribution.';
        }
    } else {
        echo 'Error: Could not get invalidation information. Could not get ' . 
            'information about the specified distribution.';
    }   
}

// Uncomment the following line to run this code in an AWS account.
// listTheInvalidations();
// snippet-end:[cloudfront.php.listinvalidation.main]
// snippet-end:[cloudfront.php.listinvalidation.complete]
// snippet-sourcedescription:[ListInvalidations.php demonstrates how to list invalidation batches in a Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[listInvalidations]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-27]
// snippet-sourceauthor:[pccornel (AWS)]