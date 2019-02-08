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
 * ABOUT THIS PHP SAMPLE => This sample is part of the SDK for PHP Developer Guide topic at
 *
 *
 */
// snippet-start:[workdocs.php.list_folders.complete]
// snippet-start:[workdocs.php.list_folders.import]

require 'vendor/autoload.php';

use Aws\WorkDocs\WorkDocsClient; 
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


try {
    $result = $client->$result = $client->getResources([]);
    foreach($result['Folders'] as $folder){
            print("<p>Folder - <b>" . $folder['Name'] . "</b> , id - <b>" . $folder['Id'] . "</b> , Parent Folder - " . $folder['ParentFolderId'] . "</p>");
        }
    var_dump($result);


} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}


// snippet-end:[workdocs.php.list_folders.main]
// snippet-end:[workdocs.php.list_folders.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetFolders.php demonstrates how to list folders currently in your Amazon WorkDocs.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getResources]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[jschwarzwalder (AWS)]