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
// snippet-start:[cognito.php.user_pool.new_admin_user.complete]
// snippet-start:[cognito.php.user_pool.new_admin_user.import]

require 'vendor/autoload.php';

use Aws\CognitoIdentityProvider\CognitoIdentityProviderClient;
use Aws\Exception\AwsException;

// snippet-end:[cognito.php.user_pool.new_admin_user.import]

/**
 * Add an new User with admin permissions inside User Pool for your AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

 // snippet-start:[cognito.php.user_pool.new_admin_user.main]
 
$client = new CognitoIdentityProviderClient([
    'profile' => 'default',
    'region' => 'us-east-2',
    'version' => '2016-04-18'
]);

$userPoolId = "us-east-2_P0oL1D";
$name = "php_sdk_admin";
$verification_email = "email@example.com";
$sendWelcomeMessage = "SUPPRESS";

try {
    $result = $client->adminCreateUser([
        'UserPoolId' => $userPoolId, 
        'Username' => $name, 
        //'UserAttributes' => [
        //    'Name' => $name
        //    'Value' => $verification_email,
        //],
        //'MessageAction' => $sendWelcomeMessage
    ]);
    
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
    error_log($e->getMessage());
}

// snippet-end:[cognito.php.user_pool.new_admin_user.main]
// snippet-end:[cognito.php.user_pool.new_admin_user.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateAdminUser.php demonstrates how to add a new admin to your unity Pool.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cognito]
// snippet-service:[cognito]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-29]
// snippet-sourceauthor:[jschwarzwalder (AWS)]