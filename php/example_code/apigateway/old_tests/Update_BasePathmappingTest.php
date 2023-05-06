<?php

/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/*
Relies on PHPUnit to test the functionality in ./Update_BasePathmapping.php.
Related custom constants are defined in ./phpunit.xml.
Example PHPUnit run command from this file's parent directory:
./vendor/bin/phpunit --testsuite apigateway-updatebasepathmapping
*/

namespace APIGateway;

use Aws\ApiGateway\ApiGatewayClient;
use Aws\MockHandler;
use Aws\Result;
use PHPUnit\Framework\TestCase;

class UpdateBasePathMappingTest extends TestCase
{
    public function testUpdatesTheBasePathMapping()
    {
        require(__DIR__ . '/../Update_BasePathmapping.php');

        $patchOperations = array([
            'op' => 'replace',
            'path' => '/stage',
            'value' => 'stage2'
        ]);

        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $apiGatewayClient = new ApiGatewayClient([
            'profile' => AWS_ACCOUNT_PROFILE_NAME,
            'region' => AWS_REGION_ID,
            'version' => API_GATEWAY_API_VERSION,
            'handler' => $mock
        ]);

        $this->assertEquals(updateBasePathMapping(
            $apiGatewayClient,
            API_GATEWAY_BASE_PATH,
            API_GATEWAY_DOMAIN_NAME,
            $patchOperations
        ), 'The updated base path\'s URI is: ' .
            'https://apigateway.us-east-1.amazonaws.com/domainnames/example.com/basepathmappings/%28none%29');
    }
}
