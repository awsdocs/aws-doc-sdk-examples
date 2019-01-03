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
//snippet-start:[securityhub.php.disable_securityhub.complete]
//snippet-start:[securityhub.php.disable_securityhub.import]

require 'vendor/autoload.php';

use Aws\SecurityHub\SecurityHubClient; 
use Aws\Exception\AwsException;
//snippet-end:[securityhub.php.disable_securityhub.import]


//Create a Securty Hub Client
$client = new Aws\SecurityHub\SecurityHubClient([
    'profile' => 'default',
    'version' => '2018-10-26',
    'region' => 'us-east-2'
]);

try {
    $result = $client->disableSecurityHub([]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}  
//snippet-end:[securityhub.php.disable_securityhub.main]
//snippet-end:[securityhub.php.disable_securityhub.complete]
//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[DisableSecurityHub.php demonstrates how to turn off AWS Security Hub.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[AWS Security Hub]
//snippet-keyword:[Code Sample]
//snippet-service:[securityhub]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-09]
//snippet-sourceauthor:[AWS]

