<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'vendor/autoload.php';

use Aws\CloudFront\CloudFrontClient;
use Aws\Exception\AwsException;


/**
 * Creating an Amazon CloudFront Distribution.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a CloudFront Client
$client = new Aws\CloudFront\CloudFrontClient([
    'profile' => 'default',
    'version' => '2018-06-18',
    'region' => 'us-east-2'
]);


$alias = [
    'Quantity' => 0,
];
$cache = [
    'Quantity' => 0,
];
$cacheBehavior = [

    'AllowedMethods' => [
        'CachedMethods' => [
            'Items' => ['HEAD', 'GET'], // REQUIRED
            'Quantity' => 2, // REQUIRED
        ],
        'Items' => ['HEAD', 'GET'], // REQUIRED
        'Quantity' => 2, // REQUIRED
    ],
    'Compress' => false,
    'DefaultTTL' => 0,
    'FieldLevelEncryptionId' => '',
    'ForwardedValues' => [ // REQUIRED
        'Cookies' => [ // REQUIRED
            'Forward' => 'none', // REQUIRED
        ],
        'Headers' => [
            'Quantity' => 0, // REQUIRED
        ],
        'QueryString' => false, // REQUIRED
        'QueryStringCacheKeys' => [
            'Quantity' => 0, // REQUIRED
        ],
    ],
    'LambdaFunctionAssociations' => ['Quantity' => 0],
    'MaxTTL' => 0,
    'MinTTL' => 0, // REQUIRED
    'SmoothStreaming' => false,
    'TargetOriginId' => 'S3-Bucket', // REQUIRED
    'TrustedSigners' => [ // REQUIRED
        'Enabled' => false, // REQUIRED
        'Quantity' => 0, // REQUIRED
    ],
    'ViewerProtocolPolicy' => 'allow-all', // REQUIRED
];

$callerReference = 'string'; // REQUIRED
$comment = 'Created by AWS SDK for PHP'; // REQUIRED
$customError = [
    'Quantity' => 0, // REQUIRED
];
$enabled = true;
$httpVersion = 'http1.1';
$IPV6 = false;
$logging = [
    'Bucket' => '', // REQUIRED
    'Enabled' => false, // REQUIRED
    'IncludeCookies' => false, // REQUIRED
    'Prefix' => '', // REQUIRED
];
$priceClass = 'PriceClass_100';
$restrictions = [
    'GeoRestriction' => [ // REQUIRED
        'Quantity' => 0, // REQUIRED
        'RestrictionType' => 'none', // REQUIRED
    ],
];
$viewerCert = [
    'CertificateSource' => 'cloudfront',
    'CloudFrontDefaultCertificate' => true,
    'MinimumProtocolVersion' => 'TLSv1',
];
$webACLid = '';

// $rootObject = '<string>';
$origin = [
    'Items' => [
        [
            'DomainName' => '<<REPLACE>>.s3.amazonaws.com', // REQUIRED
            'Id' => 'S3-Bucket', // REQUIRED
            'OriginPath' => '',
            'CustomHeaders' => ['Quantity' => 0],
            'S3OriginConfig' => ['OriginAccessIdentity' => ''],

        ],
    ],
    'Quantity' => 1, // REQUIRED
];

$distribution = [
    'CacheBehaviors' => $cache, //REQUIRED //PHP
    'CallerReference' => $callerReference, // REQUIRED
    'Comment' => $comment, // REQUIRED
    'DefaultCacheBehavior' => $cacheBehavior, // REQUIRED //PHP
    // 'DefaultRootObject' => $rootObject,
    'Enabled' => $enabled, // REQUIRED
    'Origins' => $origin, // REQUIRED //PHP
    'Aliases' => $alias, //PHP
    'CustomErrorResponses' => $customError, //PHP
    'HttpVersion' => $httpVersion,
    'IsIPV6Enabled' => $IPV6,
    'Logging' => $logging, //PHP
    'PriceClass' => $priceClass,
    'Restrictions' => $restrictions, //PHP
    'ViewerCertificate' => $viewerCert,
    'WebACLId' => $webACLid,
];

try {
    $result = $client->createDistribution([
        'DistributionConfig' => $distribution, //REQUIRED
    ]);
    var_dump(result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}