<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.getinvalidation.complete]
// snippet-start:[cloudfront.php.getinvalidation.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.getinvalidation.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets information about an invalidation for an 
 * Amazon CloudFront distribution.
 *
 * Prerequisites: An existing Amazon CloudFront distribution and a 
 * corresponding invalidation.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * - $distributionId: The distribution's ID.
 * - $invalidationId: The invalidation ID.
 *
 * Returns: Information about the invalidation; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.getinvalidation.main]
function getInvalidation($cloudFrontClient, $distributionId, $invalidationId)
{
    try {
        $result = $cloudFrontClient->getInvalidation([
            'DistributionId' => $distributionId,
            'Id' => $invalidationId,
        ]);

        $message = '';

        if (isset($result['Invalidation']['Status']))
        {
            $message = 'The status for the invalidation with the ID of ' . 
                $result['Invalidation']['Id'] . ' is ' . 
                $result['Invalidation']['Status'];
        } 
        
        if (isset($result['@metadata']['effectiveUri']))
        {
            $message .= ', and the effective URI is ' . 
                $result['@metadata']['effectiveUri'] . '.';
        } else {
            $message = 'Error: Could not get information about ' .
                'the invalidation. The invalidation\'s status ' .
                'was not available.';
        }
        
        return $message;

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function getsAnInvalidation()
{
    $distributionId = 'E1BTGP2EXAMPLE';
    $invalidationId = 'I1CDEZZEXAMPLE';

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);
    
    echo getInvalidation($cloudFrontClient, $distributionId, $invalidationId);
}

// Uncomment the following line to run this code in an AWS account.
// getsAnInvalidation();
// snippet-end:[cloudfront.php.getinvalidation.main]
// snippet-end:[cloudfront.php.getinvalidation.complete]
// snippet-sourcedescription:[ GetInvalidation.php demonstrates how to retrieve information about an invalidation issued for an Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getInvalidation]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-24]
// snippet-sourceauthor:[pccornel (AWS)]