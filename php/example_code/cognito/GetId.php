<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html
 *
 */
// snippet-start:[cognito.php.identity_pool.get_id.complete]
// snippet-start:[cognito.php.identity_pool.get_id.import]

require 'vendor/autoload.php';

use Aws\CognitoIdentity\CognitoIdentityClient;
use Aws\Exception\AwsException;

// snippet-end:[cognito.php.identity_pool.get_id.import]

/**
 *  Adds a login provider to an Identity Pool for your AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

 // snippet-start:[cognito.php.identity_pool.get_id.main]
 
$identityClient = new CognitoIdentityClient([
    'profile' => 'default',
    'region' => 'us-east-2',
    'version' => '2014-06-30'
]);

//$AccountID = "PHP_SDK_test_identity_pool";
$identityPoolid = "us-east-2:1234567890112-abcdefg";
$appid = "amzn1.application-oa2-client.1234567890112-abcdefg";

$facebook = "graph.facebook.com";
$google = "accounts.google.com";
$amazon = "www.amazon.com";
$twitter = "api.twitter.com";
$digits = "www.digits.com";
$cognigoUserPool = 'cognito-idp.us-east-1.amazonaws.com/'

try {
    $result = $identityClient->getId([
        //'AllowUnauthenticatedIdentities' => false, 
        //'IdentityPoolName' => $identityPoolName, 
        'IdentityPoolId' => $identityPoolid,
        'Logins' => [$cognigoUserPool => $appid ]
    ]);
    echo $result["IdentityPoolId"]["ClientId"];
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
    error_log($e->getMessage());
}

// snippet-end:[cognito.php.identity_pool.get_id.main]
// snippet-end:[cognito.php.identity_pool.get_id.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetID.php demonstrates how to retrieve the ClientID from an identity provider.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cognito]
// snippet-service:[cognito]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-29]
// snippet-sourceauthor:[jschwarzwalder (AWS)]