<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./GetInvalidation.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-getinvalidation
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class GetInvalidationTest extends TestCase
{
    public function testGetsAnInvalidation()
    {
        require('./GetInvalidation.php');

        $distributionId = CLOUDFRONT_DISTRIBUTION_ID;
        $invalidationId = CLOUDFRONT_INVALIDATION_ID;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = getInvalidation($cloudFrontClient, 
            $distributionId, $invalidationId);

        $this->assertStringContainsString(
            'https://cloudfront.amazonaws.com/' . 
            CLOUDFRONT_VERSION . 
            '/distribution/'. 
            CLOUDFRONT_DISTRIBUTION_ID . 
            '/invalidation/' . 
            CLOUDFRONT_INVALIDATION_ID, 
            $result
        );
    }
}