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
 * Purpose: Gets coookie signing information that viewers need in order to 
 * access restricted content in a specially-configured Amazon CloudFront 
 * distribution.
 *
 * Prerequisites: A CloudFront distribution that is specially configured for 
 * restricted access, and a CloudFront key pair. For more information, see 
 * "Serving Private Content with Signed URLs and Signed Cookies" in the 
 * Amazon CloudFront Developer Guide.
 * 
 * Inputs:
 * - $cloudFrontClient: An initialized CloudFront client.
 * - $resourceKey: A CloudFront URL to the restricted content.
 * - $expires: The expiration date and time for access requests, in 
 *   UTC Unix timestamp format.
 * - $privateKey: The path to the CloudFront private key file, in .pem format.
 * - $keyPairId: The corresponding CloudFront key pair ID.
 * 
 * Returns: Information about required Set-Cookie headers for cookie signing; 
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

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
    CloudFront-Expires = 1589926678
    CloudFront-Signature = Lv1DyC2q...2HPXaQ__
    CloudFront-Key-Pair-Id = APKAJIKZATYYYEXAMPLE
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
// snippet-sourcedate:[2020-05-20]
// snippet-sourceauthor:[pccornel (AWS)]
