<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignPrivateDistribution.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-signprivatedistribution
*/

namespace Cloudfront;

use Aws\CloudFront\CloudFrontClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class SignPrivateDistributionTest extends TestCase
{
    public function testSignsTheURL()
    {
        require(__DIR__ . '/../SignPrivateDistribution.php');

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $resourceKey = CLOUDFRONT_RESOURCE_KEY;
        $expires = time() + 300; // 5 minutes (5 * 60 seconds) from now.
        $privateKey = dirname(__DIR__) . '/tests/my-private-key.pem';
        $keyPairId = CLOUDFRONT_KEY_PAIR_ID;

        $result = signPrivateDistribution(
            $cloudFrontClient,
            $resourceKey,
            $expires,
            $privateKey,
            $keyPairId
        );

        $this->assertStringContainsStringIgnoringCase($resourceKey, $result);
    }
}
