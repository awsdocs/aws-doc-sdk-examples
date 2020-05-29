<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.listdistribution.complete]
// snippet-start:[cloudfront.php.listdistribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.listdistribution.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets information about Amazon CloudFront distributions.
 *
 * Prerequisites: At least one existing Amazon CloudFront distribution.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client 
 *   for CloudFront.
 * 
 * Returns: Information about existing distributions; otherwise, 
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.listdistribution.main]
function listDistributions($cloudFrontClient)
{
    try {
        $result = $cloudFrontClient->listDistributions([]);
        return $result;
    } catch (AwsException $e) {
        exit('Error: ' . $e->getAwsErrorMessage());
    }
}

function listTheDistributions()
{
    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-2'
    ]);

    $distributions = listDistributions($cloudFrontClient);

    if (count($distributions) == 0)
    {
        echo 'Could not find any distributions.';
    } else {
        foreach ($distributions['DistributionList']['Items'] as $distribution)
        {
            echo 'The distribution with the ID of ' . $distribution['Id'] . 
                ' has the status of ' . $distribution['Status'] . '.' . "\n";
        }
    }
}

// Uncomment the following line to run this code in an AWS account.
// listTheDistributions();
// snippet-end:[cloudfront.php.listdistribution.main]
// snippet-end:[cloudfront.php.listdistribution.complete]
// snippet-sourcedescription:[ListDistributions.php demonstrates how to list Amazon CloudFront distributions.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[listDistributions]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-24]
// snippet-sourceauthor:[pccornel (AWS)]