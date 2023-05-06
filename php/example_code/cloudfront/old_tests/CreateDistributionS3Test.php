<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./CreateDistributionS3.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-createdistributions3
*/

namespace Cloudfront;

use Aws\CloudFront\CloudFrontClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class CreateDistributionS3Test extends TestCase
{
    public function testCreatesAnS3Distribution()
    {
        require(__DIR__ . '/../CreateDistributionS3.php');

        $originName = 'my-unique-origin-name';
        $s3BucketURL = 'my-bucket-name.s3.amazonaws.com';
        $callerReference = 'my-unique-caller-reference';
        $comment = 'my-comment-about-this-distribution';
        $defaultCacheBehavior = [
            'AllowedMethods' => [
                'CachedMethods' => [
                    'Items' => ['HEAD', 'GET'],
                    'Quantity' => 2
                ],
                'Items' => ['HEAD', 'GET'],
                'Quantity' => 2
            ],
            'Compress' => false,
            'DefaultTTL' => 0,
            'FieldLevelEncryptionId' => '',
            'ForwardedValues' => [
                'Cookies' => [
                    'Forward' => 'none'
                ],
                'Headers' => [
                    'Quantity' => 0
                ],
                'QueryString' => false,
                'QueryStringCacheKeys' => [
                    'Quantity' => 0
                ],
            ],
            'LambdaFunctionAssociations' => ['Quantity' => 0],
            'MaxTTL' => 0,
            'MinTTL' => 0,
            'SmoothStreaming' => false,
            'TargetOriginId' => $originName,
            'TrustedSigners' => [
                'Enabled' => false,
                'Quantity' => 0
            ],
            'ViewerProtocolPolicy' => 'allow-all'
        ];
        $enabled = false;
        $origin = [
            'Items' => [
                [
                    'DomainName' => $s3BucketURL,
                    'Id' => $originName,
                    'OriginPath' => '',
                    'CustomHeaders' => ['Quantity' => 0],
                    'S3OriginConfig' => ['OriginAccessIdentity' => '']
                ]
            ],
            'Quantity' => 1
        ];
        $distribution = [
            'CallerReference' => $callerReference,
            'Comment' => $comment,
            'DefaultCacheBehavior' => $defaultCacheBehavior,
            'Enabled' => $enabled,
            'Origins' => $origin
        ];

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = createS3Distribution($cloudFrontClient, $distribution);

        $this->assertStringContainsString(
            'https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION . '/distribution',
            $result
        );
    }
}
