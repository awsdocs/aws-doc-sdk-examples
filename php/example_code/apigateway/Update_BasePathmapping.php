<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[apigateway.php.update_base_path_mapping.complete]
// snippet-start:[apigateway.php.update_base_path_mapping.import]
require 'vendor/autoload.php';

use Aws\ApiGateway\ApiGatewayClient;
use Aws\Exception\AwsException;
// snippet-end:[apigateway.php.update_base_path_mapping.import]

/* ////////////////////////////////////////////////////////////////////////////
 *
 * Purpose: Updates the base path mapping for a custom domain name
 * in Amazon API Gateway.
 * 
 * Inputs:
 * - $apiGatewayClient: An initialized AWS SDK for PHP API client for 
 *   API Gateway.
 * - $basePath: The base path name that callers must provide as part of the 
 *   URL after the domain name.
 * - $domainName: The custom domain name for the base path mapping.
 * - $patchOperations: The base path update operations to apply.
 * 
 * Returns: Information about the updated base path mapping, if available; 
 * otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[apigateway.php.update_base_path_mapping.main]
function updateBasePathMapping($apiGatewayClient, $basePath, $domainName, 
    $patchOperations)
{
    try {
        $result = $apiGatewayClient->updateBasePathMapping([
            'basePath' => $basePath,
            'domainName' => $domainName,
            'patchOperations' => $patchOperations
        ]);
        return 'The updated base path\'s URI is: ' .
            $result['@metadata']['effectiveUri'];
    } catch (AwsException $e) {
        return 'Error: ' . $e['message'];
    }
}

function updateTheBasePathMapping()
{
    $patchOperations = array([
        'op' => 'replace',
        'path' => '/stage',
        'value' => 'stage2'
    ]);

    $apiGatewayClient = new ApiGatewayClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2015-07-09'
    ]);

    echo updateBasePathMapping(
        $apiGatewayClient,
        '(none)', 
        'example.com',
        $patchOperations);
}

// Uncomment the following line to run this code in an AWS account.
// updateTheBasePathMapping();
// snippet-end:[apigateway.php.update_base_path_mapping.main]
// snippet-end:[apigateway.php.update_base_path_mapping.complete]
// snippet-sourcedescription:[Update_BasePathmapping.php demonstrates how to change a base path mapping in Amazon API Gateway.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon API Gateway]
// snippet-service:[apigateway]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-04-02]
// snippet-sourceauthor:[pccornel (AWS)]

