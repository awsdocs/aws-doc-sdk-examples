<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignCookiePolicy.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-signcookiepolicy
*/

namespace Cloudfront;

use Aws\CloudFront\CloudFrontClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class SignCookiePolicyTest extends TestCase
{
    public function testSignsTheCookie()
    {
        require(__DIR__ . '/../SignCookiePolicy.php');

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

        $customPolicy = <<<POLICY
{
    "Statement": [
        {
            "Resource": "{$resourceKey}",
            "Condition": {
                "IpAddress": {"AWS:SourceIp": "192.0.2.0/24"},
                "DateLessThan": {"AWS:EpochTime": {$expires}}
            }
        }
    ]
}
POLICY;

        $privateKey = dirname(__DIR__) . '/tests/my-private-key.pem';
        $keyPairId = CLOUDFRONT_KEY_PAIR_ID;

        $result = signCookiePolicy(
            $cloudFrontClient,
            $customPolicy,
            $privateKey,
            $keyPairId
        );

        $this->assertArrayHasKey('CloudFront-Signature', $result);
    }
}
