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
 * For more information about creating a WorkDocs application see the WorkDocs Developer Guide at
 * https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html
 *
 *
 */
// snippet-start:[workdocs.php.get_folder.complete]
// snippet-start:[workdocs.php.get_folder.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\WorkDocs\WorkDocsClient;

// snippet-end:[workdocs.php.get_folder.import]


/**
 * Get metadata of a folder currently in your Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// Create a workdocs Client 
// snippet-start:[workdocs.php.get_folder.main]
$client = new Aws\WorkDocs\WorkDocsClient([
    'profile' => 'default',
    'version' => '2016-05-01',
    'region' => 'us-east-2'
]);

$auth_token_file_path = 'token.txt';
$folder = 'folderid';

try {
    $file = fopen($auth_token_file, 'r') or die("Unable to open file!");
    $auth_token = fread($auth_token_file, filesize($auth_token_file));
    fclose($auth_token_file);

    $result = $client->$result = $client->getFolder([
        'AuthenticationToken' => $auth_token,
        'FolderId' => $folder 
    ]);

    var_dump($result);


} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}


// snippet-end:[workdocs.php.get_folder.main]
// snippet-end:[workdocs.php.get_folder.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetFolderDetails.php demonstrates how to get metadata of a folder currently in your Amazon WorkDocs.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getFolder]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-09]
// snippet-sourceauthor:[jschwarzwalder (AWS)]