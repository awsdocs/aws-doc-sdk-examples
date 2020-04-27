<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.getdistribution.complete]
// snippet-start:[cloudfront.php.getdistribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.getdistribution.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets information about an Amazon CloudFront distribution.
 *
 * Prerequisites: An existing Amazon CloudFront distribution.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * - $distributionId: The distribution's ID.
 *
 * Returns: Information about the distribution; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.getdistribution.main]
function getDistribution($cloudFrontClient, $distributionId)
{
    try {
        $result = $cloudFrontClient->getDistribution([
            'Id' => $distributionId
        ]);

        $message = '';

        if (isset($result['Distribution']['Status']))
        {
            $message = 'The status of the distribution with the ID of ' . 
                $result['Distribution']['Id'] . ' is currently ' . 
                $result['Distribution']['Status'];
        }
        
        if (isset($result['@metadata']['effectiveUri']))
        {
            $message .= ', and the effective URI is ' . 
                $result['@metadata']['effectiveUri'] . '.';
        } else {
            $message = 'Error: Could not get the specified distribution. ' .
                'The distribution\'s status is not available.';
        }

        return $message;

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function getsADistribution()
{
    $distributionId = 'E1BTGP2EXAMPLE';

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);
    
    echo getDistribution($cloudFrontClient, $distributionId);
}

// Uncomment the following line to run this code in an AWS account.
// getsADistribution();
// snippet-end:[cloudfront.php.getdistribution.main]
// snippet-end:[cloudfront.php.getdistribution.complete]
// snippet-sourcedescription:[GetDistribution.php demonstrates how to retrieve an Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getDistribution]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-24]
// snippet-sourceauthor:[pccornel (AWS)]