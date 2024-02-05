<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 * For more information about creating a WorkDocs application see the WorkDocs Developer Guide at
 * https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html
 *
 *
 */
// snippet-start:[workdocs.php.list_folders.complete]
// snippet-start:[workdocs.php.list_folders.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[workdocs.php.list_folders.import]

/**
 * List Folders currently in your Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// Create a workdocs Client
// snippet-start:[workdocs.php.list_folders.main]
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

    $result = $client->describeRootFolders([
        'AuthenticationToken' => $authToken
    ]);
    foreach ($result['Folders'] as $folder) {
        print("Folder - " . $folder['Name'] . " , id - " . $folder['Id']);
        print(" , Parent Folder - " . $folder['ParentFolderId'] . "\n");
    }
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}

// snippet-end:[workdocs.php.list_folders.main]
// snippet-end:[workdocs.php.list_folders.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
