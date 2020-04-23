<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./GetDistribution.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudfront-getdistribution
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\CloudFront\CloudFrontClient;

class GetDistributionTest extends TestCase
{
    public function testGetsADistribution()
    {
        require('./GetDistribution.php');

        $distributionId = CLOUDFRONT_DISTRIBUTION_ID;

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $cloudFrontClient = new CloudFrontClient([
            'profile' => AWS_PROFILE,
            'version' => CLOUDFRONT_VERSION,
            'region' => AWS_REGION,
            'handler' => $mock
        ]);

        // Tests to make sure non-AWS error message is returned.
        // An AWS error message is a failure in this case.
        $this->assertEquals(getDistribution(
            $cloudFrontClient, $distributionId),
            'Error: Could not get the specified distribution. ' .
            'The distribution\'s status is not available.');
    }
}