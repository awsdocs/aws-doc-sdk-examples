<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./DeleteDistribution.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-deletedistribution
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class DeleteDistributionTest extends TestCase
{
    public function testDeletesADistribution()
    {
        require('./DeleteDistribution.php');

        $distributionId = CLOUDFRONT_DISTRIBUTION_ID;
        $eTag = CLOUDFRONT_DISTRIBUTION_ETAG;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        $this->assertEquals(deleteDistribution(
            $cloudFrontClient, $distributionId, $eTag
        ), 'The distribution at the following effective URI has ' . 
            'been deleted: ' . 'https://cloudfront.amazonaws.com/' .
            CLOUDFRONT_VERSION. '/distribution/' . 
            CLOUDFRONT_DISTRIBUTION_ID);
    }
}