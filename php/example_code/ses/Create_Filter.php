<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ses-filters.html
 *
 */
// snippet-start:[ses.php.create_filter.complete]
// snippet-start:[ses.php.create_filter.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[ses.php.create_filter.import]

//Create a SESClient
// snippet-start:[ses.php.create_filter.main]
$SesClient = new Aws\Ses\SesClient([
    'profile' => 'default',
    'version' => '2010-12-01',
    'region' => 'us-east-2'
]);

$filter_name = 'FilterName';
$ip_address_range = '10.0.0.1/24';

try {
    $result = $SesClient->createReceiptFilter([
        'Filter' => [
            'IpFilter' => [
                'Cidr' => $ip_address_range,
                'Policy' => 'Block|Allow',
            ],
            'Name' => $filter_name,
        ],
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[ses.php.create_filter.main]
// snippet-end:[ses.php.create_filter.complete]
