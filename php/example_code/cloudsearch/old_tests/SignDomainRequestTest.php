<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./SignDomainRequest.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite cloudsearch-signdomainrequest
*/

namespace Cloudsearch;

use GuzzleHttp\Client;
use PHPUnit\Framework\TestCase;

class SignDomainRequestTest extends TestCase
{
    public function testSignsADomainRequest()
    {
        require(__DIR__ . '/../SignDomainRequest.php');

        $client = new Client();

        $result = searchDomain(
            $client,
            CLOUDSEARCH_DOMAIN_NAME,
            CLOUDSEARCH_DOMAIN_ID,
            AWS_REGION_ID,
            CLOUDSEARCH_DOMAIN_SEARCH_STRING
        );

        $this->assertStringContainsString(
            'Search results:',
            $result
        );
    }
}
