<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudfront.php.private_distribution_policy.complete]
// snippet-start:[cloudfront.php.private_distribution_policy.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient;
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.private_distribution_policy.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets a signed URL that viewers need to 
 * access restricted content in a specially configured Amazon CloudFront 
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
 * - $customPolicy: A policy statement that controls the access that a signed 
 *   URL grants to a user.
 * - $privateKey: The path to the CloudFront private key file, in .pem format.
 * - $keyPairId: The corresponding CloudFront key pair ID.
 * 
 * Returns: The signed URL; otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.private_distribution_policy.main]
function signPrivateDistributionPolicy($cloudFrontClient, $resourceKey, 
    $customPolicy, $privateKey, $keyPairId)
{
    try {
        $result = $cloudFrontClient->getSignedUrl([
            'url' => $resourceKey,
            'policy' => $customPolicy,
            'private_key' => $privateKey,
            'key_pair_id' => $keyPairId
        ]);

        return $result;

    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function signAPrivateDistributionPolicy()
{
    $resourceKey = 'https://d13l49jEXAMPLE.cloudfront.net/my-file.txt';
    $expires = time() + 300; // 5 minutes (5 * 60 seconds) from now.
    $customPolicy = <<<POLICY
{
    "Statement": [
        {
            "Resource": "{$resourceKey}",
            "Condition": {
                "IpAddress": {"AWS:SourceIp": "{$_SERVER['REMOTE_ADDR']}/32"},
                "DateLessThan": {"AWS:EpochTime": {$expires}}
            }
        }
    ]
}
POLICY;
    $privateKey = dirname(__DIR__) . '/cloudfront/my-private-key.pem';
    $keyPairId = 'AAPKAJIKZATYYYEXAMPLE';
    
    $cloudFrontClient = new CloudFrontClient([
        'profile' => 'default',
        'version' => '2014-11-06',
        'region' => 'us-east-1'
    ]);
    
    echo signPrivateDistributionPolicy($cloudFrontClient, $resourceKey, 
        $customPolicy, $privateKey, $keyPairId);
}

// Uncomment the following line to run this code in an AWS account.
// signAPrivateDistributionPolicy();
// snippet-end:[cloudfront.php.private_distribution_policy.main]
// snippet-end:[cloudfront.php.private_distribution_policy.complete] 
// snippet-sourcedescription:[SignPriveDistributionPolicy.php demonstrates how to provide users access to your private content using an Amazon CloudFront distribution and custom policy.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getSignedUrl]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-22]
// snippet-sourceauthor:[pccornel (AWS)]
