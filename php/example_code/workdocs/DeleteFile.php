<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * For more information about creating a WorkDocs application see the WorkDocs Developer Guide at
 * https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html
 *
 *
 */
// snippet-start:[workdocs.php.delete_file.complete]
// snippet-start:[workdocs.php.delete_file.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[workdocs.php.delete_file.import]


/**
 * Delete a file currently in your Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// Create a workdocs Client
// snippet-start:[workdocs.php.delete_file.main]
$client = new Aws\WorkDocs\WorkDocsClient([
    'profile' => 'default',
    'version' => '2016-05-01',
    'region' => 'us-east-2'
]);

$authTokenFilePath = 'token.txt';
$document = 'documentid';

try {
    $file = fopen($authTokenFilePath, 'r');
    $authToken = fread($file, filesize($file));
    fclose($file);

    $result = $client->deleteDocument([
        'AuthenticationToken' => $authToken,
        'DocumentId' => $document
    ]);

    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[workdocs.php.delete_file.main]
// snippet-end:[workdocs.php.delete_file.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
