<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignCookie.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-signcookietest
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class SignCookieTest extends TestCase
{
    public function testSignsTheCookie()
    {
        require('./SignCookie.php');

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

        $result = $cloudFrontClient->getSignedCookie([
            'url' => $resourceKey,
            'expires' => $expires, 
            'private_key' => $privateKey,
            'key_pair_id' => $keyPairId
        ]);

        $this->assertArrayHasKey('CloudFront-Signature', $result);

        // echo $result['CloudFront-Expires'] // 1589926678
        // echo $result['CloudFront-Signature'] // 'Lv1DyC2q....2HPXaQ__'
        // echo $result['CloudFront-Key-Pair-Id'] // 'APKAJIKZATYYYEXAMPLE'
    }
}