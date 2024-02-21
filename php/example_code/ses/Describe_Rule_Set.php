<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/ses-filters.html
 *
 */
// snippet-start:[ses.php.describe_rule_set.complete]
// snippet-start:[ses.php.describe_rule_set.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[ses.php.describe_rule_set.import]

//Create a SESClient
// snippet-start:[ses.php.describe_rule_set.main]
$SesClient = new Aws\Ses\SesClient([
    'profile' => 'default',
    'version' => '2010-12-01',
    'region' => 'us-east-2'
]);

$name = 'Rule_Set_Name';

try {
    $result = $SesClient->describeReceiptRuleSet([
        'RuleSetName' => $name,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[ses.php.describe_rule_set.main]
// snippet-end:[ses.php.describe_rule_set.complete]
