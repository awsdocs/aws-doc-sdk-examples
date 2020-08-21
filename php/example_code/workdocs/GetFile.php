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
// snippet-start:[workdocs.php.get_file.complete]
// snippet-start:[workdocs.php.get_file.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\WorkDocs\WorkDocsClient;

// snippet-end:[workdocs.php.get_file.import]


/**
 * Get retrieve details about a file currently in your Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

// Create a workdocs Client 
// snippet-start:[workdocs.php.get_file.main]
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
    fclose($authTokenFilePath);

    $result = $client->getDocument([
        'AuthenticationToken' => $authToken,
        'DocumentId' => $document
    ]);

    var_dump($result);
    
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}


// snippet-end:[workdocs.php.get_file.main]
// snippet-end:[workdocs.php.get_file.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetFile.php demonstrates how to retrieve details about a document currently in your Amazon WorkDocs.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getDocument]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-09]
// snippet-sourceauthor:[jschwarzwalder (AWS)]