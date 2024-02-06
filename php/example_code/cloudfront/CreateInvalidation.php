<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


// snippet-start:[cloudfront.php.createinvalidation.complete]
// snippet-start:[cloudfront.php.createinvalidation.import]
require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[cloudfront.php.createinvalidation.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Invalidates a cached object in an Amazon CloudFront distribution.
 *
 * Prerequisites: An existing Amazon CloudFront distribution.
 *
 * Inputs:
 * - $cloudFrontClient: An initialized AWS SDK for PHP SDK client
 *   for CloudFront.
 * - $distributionId: The distribution's ID.
 * - $callerReference: Any value that uniquely identifies this request.
 * - $paths: The list of paths to the cached objects you want to invalidate.
 *   For more information, see "Specifying the Objects to Invalidate" in the
 *   Amazon CloudFront Developer Guide.
 * - $quantity: The number of invalidation paths specified.
 *
 * Returns: Information about the invalidation request; otherwise,
 * the error message.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudfront.php.createinvalidation.main]
function createInvalidation(
    $cloudFrontClient,
    $distributionId,
    $callerReference,
    $paths,
    $quantity
) {
    try {
        $result = $cloudFrontClient->createInvalidation([
            'DistributionId' => $distributionId,
            'InvalidationBatch' => [
                'CallerReference' => $callerReference,
                'Paths' => [
                    'Items' => $paths,
                    'Quantity' => $quantity,
                ],
            ]
        ]);

        $message = '';

        if (isset($result['Location'])) {
            $message = 'The invalidation location is: ' . $result['Location'];
        }

        $message .= ' and the effective URI is ' . $result['@metadata']['effectiveUri'] . '.';

        return $message;
    } catch (AwsException $e) {
        return 'Error: ' . $e->getAwsErrorMessage();
    }
}

function createTheInvalidation()
{
    $distributionId = 'E17G7YNEXAMPLE';
    $callerReference = 'my-unique-value';
    $paths = ['/*'];
    $quantity = 1;

    $cloudFrontClient = new Aws\CloudFront\CloudFrontClient([
        'profile' => 'default',
        'version' => '2018-06-18',
        'region' => 'us-east-1'
    ]);

    echo createInvalidation(
        $cloudFrontClient,
        $distributionId,
        $callerReference,
        $paths,
        $quantity
    );
}

// Uncomment the following line to run this code in an AWS account.
// createTheInvalidation();
// snippet-end:[cloudfront.php.createinvalidation.main]
// snippet-end:[cloudfront.php.createinvalidation.complete]
