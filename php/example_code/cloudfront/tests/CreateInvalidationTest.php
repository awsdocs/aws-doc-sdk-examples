<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./CreateInvalidation.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-createinvalidation
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class CreateInvalidationTest extends TestCase
{
    public function testCreatesAnInvalidation()
    {
        require('./CreateInvalidation.php');

        $distributionId = CLOUDFRONT_DISTRIBUTION_ID;
        $callerReference = CLOUDFRONT_CALLER_REFERENCE;
        $paths = ['/*'];
        $quantity = 1;
        
        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $this->assertEquals(createInvalidation(
            $cloudFrontClient,
            $distributionId,
            $callerReference,
            $paths,
            $quantity
        ), 'The invalidation location is: ' . "\n" . 
            'The effective URI is: ' .
            'https://cloudfront.amazonaws.com/'. CLOUDFRONT_VERSION . 
            '/distribution/' . CLOUDFRONT_DISTRIBUTION_ID . '/invalidation');
    }
}