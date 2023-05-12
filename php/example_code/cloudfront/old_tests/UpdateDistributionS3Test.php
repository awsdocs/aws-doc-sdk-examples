<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./UpdateDistributionS3.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-updatedistribution
*/

namespace Cloudfront;

use Aws\CloudFront\CloudFrontClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class UpdateDistributionTest extends TestCase
{
    public static function setUpBeforeClass(): void
    {
        require(__DIR__ . '/../UpdateDistributionS3.php');
    }

    protected function setUp(): void
    {
        global $cloudFrontClient;
        global $distributionId;
        global $eTag;
        global $distributionConfig;

        $distributionId = CLOUDFRONT_DISTRIBUTION_ID;
        $eTag = CLOUDFRONT_DISTRIBUTION_ETAG;

        $distributionConfig = [
            'CacheBehaviors' => [
                'Quantity' => 0
            ],
            'CallerReference' => 'my-',
            'Comment' => '',
            'DefaultCacheBehavior' => [
                'TargetOriginId' => 'my-target-origin-id',
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
                    ]
                ],
                'ViewerProtocolPolicy' => 'allow-all',
                'MinTTL' => '0',
                'AllowedMethods' => [
                    'CachedMethods' => [
                        'Items' => ['HEAD', 'GET'],
                        'Quantity' => 2
                    ],
                    'Items' => ['HEAD', 'GET'],
                    'Quantity' => 2
                ],
                'SmoothStreaming' => false,
                'DefaultTTL' => '86400',
                'MaxTTL' => '31536000',
                'Compress' => false,
                'LambdaFunctionAssociations' => [
                    'Quantity' => 0
                ],
                'FieldLevelEncryptionId' => '',
                'TrustedSigners' => [
                    'Enabled' => false,
                    'Quantity' => 0
                ]
            ],
            'DefaultRootObject' => '',
            'Enabled' => false,
            'Origins' => [
                'Items' => [
                    [
                        'DomainName' => 'my-bucket-name.s3.amazonaws.com',
                        'Id' => 'my-unique-origin-name',
                        'OriginPath' => '',
                        'CustomHeaders' => [
                            'Quantity' => 0
                        ],
                        'S3OriginConfig' => [
                            'OriginAccessIdentity' => ''
                        ]
                    ]
                ],
                'Quantity' => 1
            ],
            'Aliases' => [
                'Quantity' => 0
            ],
            'CustomErrorResponses' => [
              'Quantity' => 0
            ],
            'HttpVersion' => 'http2',
            'Logging' => [
                'Enabled' => false,
                'IncludeCookies' => false,
                'Bucket' => '',
                'Prefix' => ''
            ],
            'PriceClass' => 'PriceClass_All',
            'Restrictions' => [
                'GeoRestriction' => [
                    'RestrictionType' => 'none',
                    'Quantity' => 0
                ]
            ],
            'ViewerCertificate' => [
                'CloudFrontDefaultCertificate' => true,
                'MinimumProtocolVersion' => 'TLSv1',
                'CertificateSource' => 'cloudfront',
                'WebACLId' => ''
            ]
        ];

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);
    }

    protected function tearDown(): void
    {
        unset($GLOBALS['cloudFrontClient']);
    }

    public function testUpdatesTheDistribution()
    {
        $result = updateDistribution(
            $GLOBALS['cloudFrontClient'],
            $GLOBALS['distributionId'],
            $GLOBALS['distributionConfig'],
            $GLOBALS['eTag']
        );

        $this->assertStringContainsString(
            'https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION . '/distribution/' .
            CLOUDFRONT_DISTRIBUTION_ID . '/config',
            $result
        );
    }

    public function testGetsADistributionConfig()
    {
        $result = getDistributionConfig(
            $GLOBALS['cloudFrontClient'],
            $GLOBALS['distributionId']
        );

        $this->assertContains('https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION . '/distribution/' .
            CLOUDFRONT_DISTRIBUTION_ID, $result);
    }

    public function testGetsADistributionETag()
    {
        $result = getDistributionETag(
            $GLOBALS['cloudFrontClient'],
            $GLOBALS['distributionId']
        );

        $this->assertContains('https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION . '/distribution/' .
            CLOUDFRONT_DISTRIBUTION_ID, $result);
    }
}
