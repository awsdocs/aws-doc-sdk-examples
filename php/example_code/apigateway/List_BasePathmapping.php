<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

/* ////////////////////////////////////////////////////////////////////////////

Purpose:
  Gets the base path mappings for a custom domain name in Amazon API Gateway.

Prerequisites:
  - You must have an AWS account. For more information, see "How do I create
    and activate a new Amazon Web Services account" on the AWS Premium Support
    website.
  - This code uses default AWS access credentials. For more information, see
    "Credentials for the AWS SDK for PHP" in the AWS SDK for PHP Developer 
    Guide.
  - You must have a custom domain name in API Gateway. For more information, 
    see "Custom Domain Names" in the Amazon API Gateway Developer Guide.

Running the code:
  To run this code, use PHPUnit along with the phpunit.xml file in this folder.
  For example:

  ./vendor/bin/phpunit --testsuite apigateway-listbasepathmapping

Additional information:
  - As an AWS best practice, grant this code least privilege, or only the 
    permissions required to perform a task. For more information, see 
    "Grant Least Privilege" in the AWS Identity and Access Management 
    User Guide.
  - This code has not been tested in all AWS Regions. Some AWS services are 
    available only in specific Regions. For more information, see the 
    "AWS Regional Table" on the AWS website.
  - Running this code outside of the included PHPUnit test might result in 
    charges to your AWS account.

//////////////////////////////////////////////////////////////////////////// */

// snippet-start:[apigateway.php.list_base_path_mapping.complete]
// snippet-start:[apigateway.php.list_base_path_mapping.import]
require 'vendor/autoload.php';

use Aws\ApiGateway\ApiGatewayClient;
use Aws\Exception\AwsException;
//snippet-end:[apigateway.php.list_base_path_mapping.import]

//snippet-start:[apigateway.php.list_base_path_mapping.main]
class ListBathPathMappingsExample
{
    private $apiGatewayClient = null;

    public function __construct($apiGatewayClient)
    {
        $this->apiGatewayClient = $apiGatewayClient;
    }
    
    /* ////////////////////////////////////////////////////////////////////////

    Purpose: Gets the base path mappings for a custom domain name in 
    Amazon API Gateway.

    Inputs:
      - $domainName: The custom domain name for the base path mappings.

    Returns: true if the API call succeeds; otherwise, false.

    //////////////////////////////////////////////////////////////////////// */
    public function listBasePathMappings($domainName)
    {
        try {
            $result = $this->apiGatewayClient->getBasePathMappings([
                'domainName' => $domainName
            ]);
            var_dump($result);
        } catch (AwsException $e) {
            echo $e->getMessage();
            echo "\n";
            return false;
        }

        if ($result['@metadata']['statusCode'] == 200) {
            return true;
        } else {
            return false;
        }
    }
}
// snippet-end:[apigateway.php.list_base_path_mapping.main]
// snippet-end:[apigateway.php.list_base_path_mapping.complete]

use PHPUnit\Framework\TestCase;
use Aws\MockHandler;
use Aws\Result;

# Relies on PHPUnit to test the functionality in the preceding code.
# Related custom constants are defined in the phpunit.xml file in this folder.
class ListBathPathMappingsExampleTest extends TestCase
{
    public function testListsTheBasePathMappings()
    {
        $mock = new MockHandler();
        $mock->append(new Result(array(true)));

        $this->apiGatewayClientMock = new ApiGatewayClient([
            'profile' => 'default',
            'region' => AWS_REGION,
            'version' => '2015-07-09',
            'handler' => $mock
        ]);

        $this->listBathPathMappingsExample = new ListBathPathMappingsExample(
            $this->apiGatewayClientMock);

        $this->assertEquals($this->listBathPathMappingsExample->
            listBasePathMappings(API_GATEWAY_DOMAIN_NAME), true);
    }
}
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[List_BasePathmapping.php demonstrates how to list Base Path Mappings in a given domain in API Gateway.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon API Gateway]
// snippet-service:[apigateway]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-03-31]
// snippet-sourceauthor:[pccornel (AWS)]

