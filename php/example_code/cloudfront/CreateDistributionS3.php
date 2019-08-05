<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 *
 *
 */
// snippet-start:[cloudfront.php.creates3distribution.complete]
// snippet-start:[cloudfront.php.creates3distribution.import]
require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudfront.php.creates3distribution.import]

/**
 * Creating an Amazon CloudFront Distribution for an S3 Bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a CloudFront Client
// snippet-start:[cloudfront.php.creates3distribution.main] 
$client = new Aws\CloudFront\CloudFrontClient([
    'profile' => 'default',
    'version' => '2018-06-18',
    'region' => 'us-east-2'
]);

$originName = 'Name to identify the S3 bucket';
$s3BucketURL = '<bucket-name>.s3.amazonaws.com';
$callerReference = 'unique string';
$comment = 'Created by AWS SDK for PHP';
$cacheBehavior = [

    'AllowedMethods' => [
        'CachedMethods' => [
            'Items' => ['HEAD', 'GET'],
            'Quantity' => 2,
        ],
        'Items' => ['HEAD', 'GET'],
        'Quantity' => 2,
    ],
    'Compress' => false,
    'DefaultTTL' => 0,
    'FieldLevelEncryptionId' => '',
    'ForwardedValues' => [
        'Cookies' => [
            'Forward' => 'none',
        ],
        'Headers' => [
            'Quantity' => 0,
        ],
        'QueryString' => false,
        'QueryStringCacheKeys' => [
            'Quantity' => 0,
        ],
    ],
    'LambdaFunctionAssociations' => ['Quantity' => 0],
    'MaxTTL' => 0,
    'MinTTL' => 0,
    'SmoothStreaming' => false,
    'TargetOriginId' => $originName,
    'TrustedSigners' => [
        'Enabled' => false,
        'Quantity' => 0,
    ],
    'ViewerProtocolPolicy' => 'allow-all',
];
$enabled = false;
$origin = [
    'Items' => [
        [
            'DomainName' => $s3BucketURL,
            'Id' => $originName,
            'OriginPath' => '',
            'CustomHeaders' => ['Quantity' => 0],
            'S3OriginConfig' => ['OriginAccessIdentity' => ''],

        ],
    ],
    'Quantity' => 1,
];

/*
 * $cache = [
 *     'Quantity' => 0,
 * ];
 * $rootObject = '<string>';
 * $alias = [
 *     'Quantity' => 0,
 * ];
 * $customError = [
 *     'Quantity' => 0, 
 * ];
 * $httpVersion = 'http1.1';
 * $IPV6 = false;
 * $logging = [
 *     'Bucket' => '', 
 *     'Enabled' => false, 
 *     'IncludeCookies' => false, 
 *     'Prefix' => '', 
 * ];
 * $priceClass = 'PriceClass_100';
 * $restrictions = [
 *     'GeoRestriction' => [ 
 *         'Quantity' => 0, 
 *         'RestrictionType' => 'none', 
 *     ],
 * ];
 * $viewerCert = [
 *     'CertificateSource' => 'cloudfront',
 *     'CloudFrontDefaultCertificate' => true,
 *     'MinimumProtocolVersion' => 'TLSv1',
 * ];
 * $webACLid = '';
*/


$distribution = [
    'CallerReference' => $callerReference,
    'Comment' => $comment,
    'DefaultCacheBehavior' => $cacheBehavior,
    'Enabled' => $enabled,
    'Origins' => $origin,
    //'CacheBehaviors' => $cache, 
    //'DefaultRootObject' => $rootObject,
    //'Aliases' => $alias, 
    //'CustomErrorResponses' => $customError, 
    //'HttpVersion' => $httpVersion,
    //'IsIPV6Enabled' => $IPV6,
    //'Logging' => $logging, 
    //'PriceClass' => $priceClass,
    //'Restrictions' => $restrictions, 
    //'ViewerCertificate' => $viewerCert,
    //'WebACLId' => $webACLid,
];

try {
    $result = $client->createDistribution([
        'DistributionConfig' => $distribution, //REQUIRED
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[cloudfront.php.creates3distribution.main]
// snippet-end:[cloudfront.php.creates3distribution.complete] 
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateDistributionS3.php demonstrates how to create an Amazon CloudFront Distribution for an S3 Bucket. ]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[createDistribution]
// snippet-keyword:[Amazon CloudFront]
// snippet-service:[cloudfront]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-27]
// snippet-sourceauthor:[jschwarzwalder (AWS)]