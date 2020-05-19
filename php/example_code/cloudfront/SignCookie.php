<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.signed_cookie.complete]
// snippet-start:[cloudfront.php.signed_cookie.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient;
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.signed_cookie.import]

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
 * Get a signed cookie for an Amazon CloudFront Distribution.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// snippet-start:[cloudfront.php.signed_cookie.main]
function signCookie($cloudFrontClient, $resourceKey, $expires, 
    $privateKey, $keyPairId)
{
    try {
        $result = $cloudFrontClient->getSignedCookie([
            'url' => $resourceKey,
            'expires' => $expires, 
            'private_key' => $privateKey,
            'key_pair_id' => $keyPairId
        ]);

        return $result;

    } catch (AwsException $e) {
        return [ 'Error' => $e->getAwsErrorMessage() ];
    }
}

function signACookie()
{
    $resourceKey = 'https://d13l49jEXAMPLE.cloudfront.net/my-file.txt';
    $expires = time() + 300; // 5 minutes (5 * 60 seconds) from now.
    $privateKey = dirname(__DIR__) . '/cloudfront/my-private-key.pem';
    $keyPairId = 'APKAJIKZATYYYEXAMPLE';

    $cloudFrontClient = new CloudFrontClient([
        'profile' => 'default',
        'version' => '2014-11-06',
        'region' => 'us-east-1'
    ]);

    $result = signCookie($cloudFrontClient, $resourceKey, $expires, 
        $privateKey, $keyPairId);

    /* If successful, returns something like:
       [
           'CloudFront-Expires' => 1589926678,
           'CloudFront-Signature' => 'Lv1DyC2q....2HPXaQ__',
           'CloudFront-Key-Pair-Id' => 'APKAJIKZATYYYEXAMPLE'
       ]
    */
    foreach($result as $key => $value)
    {
        echo $key . ' = ' . $value . "\n";
    }
}

// Uncomment the following line to run this code in an AWS account.
// signACookie();
// snippet-end:[cloudfront.php.signed_cookie.main]
// snippet-end:[cloudfront.php.signed_cookie.complete] 
// snippet-sourcedescription:[SignCookie.php demonstrates how to grant users access to your private content using signed cookies and an Amazon CloudFront distribution.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getSignedCookie]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-18]
// snippet-sourceauthor:[pccornel (AWS)]
