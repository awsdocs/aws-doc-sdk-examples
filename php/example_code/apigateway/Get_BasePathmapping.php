<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[apigateway.php.get_base_path_mapping.complete]
// snippet-start:[apigateway.php.get_base_path_mapping.import]
require 'vendor/autoload.php';

use Aws\ApiGateway\ApiGatewayClient;
use Aws\Exception\AwsException;
// snippet-end:[apigateway.php.get_base_path_mapping.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Gets the base path mapping for a custom domain name in 
 * Amazon API Gateway.
 * 
 * Prerequisites: A custom domain name in API Gateway. For more information,
 * see "Custom Domain Names" in the Amazon API Gateway Developer Guide.
 *
 * Inputs:
 * - $apiGatewayClient: An initialized AWS SDK for PHP API client for 
 *   API Gateway.
 * - $basePath: The base path name that callers must provide as part of the 
 *   URL after the domain name.
 * - $domainName: The custom domain name for the base path mapping.
 *
 * Returns: The base path mapping, if available; otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

//snippet-start:[apigateway.php.get_base_path_mapping.main]
function getBasePathMapping($apiGatewayClient, $basePath, $domainName)
{
    try {
        $result = $apiGatewayClient->getBasePathMapping([
            'basePath' => $basePath,
            'domainName' => $domainName,
        ]);
        return 'The base path mapping\'s effective URI is: ' . 
            $result['@metadata']['effectiveUri'];
    } catch (AwsException $e) {
        return 'Error: ' . $e['message'];
    }
}

function getsTheBasePathMapping()
{
    $apiGatewayClient = new ApiGatewayClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2015-07-09'
    ]);
    
    echo getBasePathMapping($apiGatewayClient, '(none)', 'example.com');
}

// Uncomment the following line to run this code in an AWS account.
// getsTheBasePathMapping();
// snippet-end:[apigateway.php.get_base_path_mapping.main]
// snippet-end:[apigateway.php.get_base_path_mapping.complete]
// snippet-sourcedescription:[Get_BasePathmapping.php demonstrates how to view base path mapping information in Amazon API Gateway.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon API Gateway]
// snippet-service:[apigateway]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-13]
// snippet-sourceauthor:[pccornel (AWS)]

