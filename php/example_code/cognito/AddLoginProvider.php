<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html
 *
 */
// snippet-start:[cognito.php.identity_pool.add_login_provider.complete]
// snippet-start:[cognito.php.identity_pool.add_login_provider.import]

require 'vendor/autoload.php';

use Aws\CognitoIdentity\CognitoIdentityClient;
use Aws\Exception\AwsException;

// snippet-end:[cognito.php.identity_pool.add_login_provider.import]

/**
 *  Adds a login provider to an Identity Pool for your AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

 // snippet-start:[cognito.php.identity_pool.add_login_provider.main]

$identityClient = new CognitoIdentityClient([
    'profile' => 'default',
    'region' => 'us-east-2',
    'version' => '2014-06-30'
]);

$identityPoolName = "PHP_SDK_test_identity_pool";
$identityPoolid = "us-east-2:1234567890112-abcdefg";
$appid = "amzn1.application-oa2-client.1234567890112-abcdefg";

$facebook = "graph.facebook.com";
$google = "accounts.google.com";
$amazon = "www.amazon.com";
$twitter = "api.twitter.com";
$digits = "www.digits.com";


try {
    $result = $identityClient->updateIdentityPool([
        'AllowUnauthenticatedIdentities' => false,
        'IdentityPoolName' => $identityPoolName,
        'IdentityPoolId' => $identityPoolid,
        'SupportedLoginProviders' => [$amazon => $appid ]
    ]);
    echo $result["IdentityPoolId"]["ClientId"];
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
    error_log($e->getMessage());
}

// snippet-end:[cognito.php.identity_pool.add_login_provider.main]
// snippet-end:[cognito.php.identity_pool.add_login_provider.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
