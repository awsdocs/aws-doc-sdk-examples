<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./List_BasePathmapping.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite apigateway-listbasepathmapping
*/
use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;
use Aws\ApiGateway\ApiGatewayClient;

class ListBasePathMappingsTest extends TestCase
{
    public function testListsTheBasePathMappings()
    {
        require('./List_BasePathmapping.php');

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $apiGatewayClient = new ApiGatewayClient([
            'profile' => AWS_ACCOUNT_PROFILE_NAME,
            'region' => AWS_REGION_ID,
            'version' => API_GATEWAY_API_VERSION,
            'handler' => $mock
        ]);

        $this->assertEquals(listBasePathMappings(
            $apiGatewayClient, 
            API_GATEWAY_DOMAIN_NAME
        ), 'The base path mapping(s) effective URI is: ' . 
            'https://apigateway.us-east-1.amazonaws.com/domainnames/example.com/basepathmappings');
    }
}

