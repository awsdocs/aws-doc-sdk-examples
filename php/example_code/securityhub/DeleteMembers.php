<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *
 *
 *
 */
// snippet-start:[securityhub.php.delete_members.complete]
// snippet-start:[securityhub.php.delete_members.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\SecurityHub\SecurityHubClient;

// snippet-end:[securityhub.php.delete_members.import]


// Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

$awsAccounts = ['0123456789'];

try {
    $result = $client->deleteMembers([
        'AccountIds' => $awsAccounts,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}
// snippet-end:[securityhub.php.delete_members.main]
// snippet-end:[securityhub.php.delete_members.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteMembers.php demonstrates how to remove a member association with other AWS accounts from AWS Security Hub.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[AWS Security Hub]
// snippet-keyword:[Code Sample]
// snippet-service:[securityhub]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-09]
// snippet-sourceauthor:[AWS]

