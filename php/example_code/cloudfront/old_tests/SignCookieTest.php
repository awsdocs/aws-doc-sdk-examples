<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignCookie.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-signcookie
*/

namespace Cloudfront;

use Aws\CloudFront\CloudFrontClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class SignCookieTest extends TestCase
{
    public function testSignsTheCookie()
    {
        require(__DIR__ . '/../SignCookie.php');

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

        $result = signCookie(
            $cloudFrontClient,
            $resourceKey,
            $expires,
            $privateKey,
            $keyPairId
        );

        $this->assertArrayHasKey('CloudFront-Signature', $result);
    }
}
