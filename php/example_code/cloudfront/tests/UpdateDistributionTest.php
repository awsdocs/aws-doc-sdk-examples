<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignPrivateDistributionPolicy.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-signprivatedistributionpolicy
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class SignPrivateDistributionPolicyTest extends TestCase
{
    public function testListsTheInvalidations()
    {
        require('./SignPrivateDistributionPolicy.php');

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $result = listInvalidations($cloudFrontClient, 
            CLOUDFRONT_DISTRIBUTION_ID);

        $this->assertContains('https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION . '/distribution/' . 
            CLOUDFRONT_DISTRIBUTION_ID . '/invalidation', 
            $result['@metadata']);
    }
}