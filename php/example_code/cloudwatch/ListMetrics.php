<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudwatch.php.list_metrics.complete]
// snippet-start:[cloudwatch.php.list_metrics.import]
require 'vendor/autoload.php';

use Aws\CloudWatch\CloudWatchClient; 
use Aws\Exception\AwsException;
// snippet-end:[cloudwatch.php.list_metrics.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Provides information about published metrics in Amazon CloudWatch.
 * 
 * Inputs:
 * - $cloudWatchClient: An initialized CloudWatch client.
 * 
 * Returns: Information about published metrics; otherwise, the error message.
 * ///////////////////////////////////////////////////////////////////////// */
 
// snippet-start:[cloudwatch.php.list_metrics.main]
function listMetrics($cloudWatchClient)
{
    try {
        $result = $cloudWatchClient->listMetrics();

        $message = ''; 

        if (isset($result['@metadata']['effectiveUri']))
        {
            $message .= 'For the effective URI at ' . 
                $result['@metadata']['effectiveUri'] . ":\n\n";
        
            if ((isset($result['Metrics'])) and 
                (count($result['Metrics']) > 0))
            {
                $message .= "Metrics found:\n\n";

                foreach($result['Metrics'] as $metric) 
                {
                    $message .= 'For metric ' . $metric['MetricName'] . 
                        ' in namepsace ' . $metric['Namespace'] . ":\n";
                    
                    if ((isset($metric['Dimensions'])) and 
                        (count($metric['Dimensions']) > 0))
                    {
                        $message .= "Dimensions:\n";

                        foreach ($metric['Dimensions'] as $dimension)
                        {
                            $message .= 'Name: ' . $dimension['Name'] . 
                                ', Value: ' . $dimension['Value'] . "\n";
                        }

                        $message .= "\n";
                    } else {
                        $message .= "No dimensions.\n\n";
                    }
                }
            } else {
                $message .= 'No metrics found.';
            }
        } else {
            $message .= 'No metrics found.';
        }

        return $message;
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function listTheMetrics()
{
    $cloudWatchClient = new CloudWatchClient([
        'profile' => 'default',
        'region' => 'us-east-1',
        'version' => '2010-08-01'
    ]);

    echo listMetrics($cloudWatchClient);
}

// Uncomment the following line to run this code in an AWS account.
// listTheMetrics();
// snippet-end:[cloudwatch.php.list_metrics.main]
// snippet-end:[cloudwatch.php.list_metrics.complete]
// snippet-sourcedescription:[ListMetrics.php demonstrates how to retrieve a list of published Amazon CloudWatch metrics.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cloudwatch]
// snippet-service:[cloudwatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-05-08]
// snippet-sourceauthor:[pccornel (AWS)]

