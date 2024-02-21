<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* *
 * For more information about creating a WorkDocs application see the WorkDocs Developer Guide at
 * https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html
 *
 */
// snippet-start:[workdocs.php.get_current_user.complete]
// snippet-start:[workdocs.php.get_current_user.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[workdocs.php.get_current_user.import]

/**
 * Get user information for currently connected Amazon WorkDocs user.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// Create a workdocs Client
// snippet-start:[workdocs.php.get_current_user.main]
$client = new Aws\WorkDocs\WorkDocsClient([
    'profile' => 'default',
    'version' => '2016-05-01',
    'region' => 'us-east-2'
]);

$authTokenFilePath = 'token.txt';

try {
    $file = fopen($authTokenFilePath, 'r');
    $authToken = fread($file, filesize($file));
    fclose($file);
    $result = $client->getCurrentUser([
        'AuthenticationToken' => $authToken
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[workdocs.php.get_current_user.main]
// snippet-end:[workdocs.php.get_current_user.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
