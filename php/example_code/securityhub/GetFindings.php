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
// snippet-start:[securityhub.php.get_findings.complete]
// snippet-start:[securityhub.php.get_findings.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\SecurityHub\SecurityHubClient;

// snippet-end:[securityhub.php.get_findings.import]


// Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

try {
    $result = $client->getFindings([]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}
// snippet-end:[securityhub.php.get_findings.main]
// snippet-end:[securityhub.php.get_findings.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetFindings.php demonstrates how to return results from other AWS Security services like Amazon GuardDuty, Amazon Inspector, and  Amazon Macie.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[AWS Security Hub]
// snippet-keyword:[Code Sample]
// snippet-service:[securityhub]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-09]
// snippet-sourceauthor:[AWS]

