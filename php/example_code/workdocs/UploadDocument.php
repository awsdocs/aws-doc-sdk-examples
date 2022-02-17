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
// snippet-start:[workdocs.php.upload_document.complete]
// snippet-start:[workdocs.php.upload_document.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\WorkDocs\WorkDocsClient;
use GuzzleHttp\Client as httpClient;
use GuzzleHttp\Exception\ClientException;

// snippet-end:[workdocs.php.upload_document.import]


/**
 * Upload a Document to Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a workdocs Client 
// snippet-start:[workdocs.php.upload_document.main]
$client = new Aws\WorkDocs\WorkDocsClient([
    'profile' => 'default',
    'version' => '2016-05-01',
    'region' => 'us-east-2'
]);

$folder = 'folderid';
$pathtoFile = '';
$file = 'filename.txt';

$authTokenFilePath = 'token.txt';

try {
    $fileToUpload = $pathtoFile . $file;
    $file = fopen($authTokenFilePath, 'r');
    $authToken = fread($file, filesize($file));
    fclose($authTokenFilePath);

    print("Create Document\n");
    $result = $client->initiateDocumentVersionUpload([
        'AuthenticationToken' => $authToken,
        'ParentFolderId' => $folder,
        'Name' => $file,
    ]);
    var_dump($result);
    $documentID = $result['Metadata']['Id'];
    $documentVersionID = $result['Metadata']['LatestVersionMetadata']['Id'];
    $uploadurl = $result['UploadMetadata']['UploadUrl'];
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}
try {
    print("Upload Document\n");
    $body = fopen($fileToUpload, 'r');

    $guzzle = new httpClient();
    $upload = $guzzle->put($uploadurl, [
        'body' => $body
    ]);

    var_dump($upload);

} catch (ClientException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}
try {
    print("Update Document Version\n");
    $updateResult = $client->updateDocumentVersion([
        'AuthenticationToken' => $authToken,
        'DocumentId' => $documentID,
        'VersionId' => $documentVersionID,
        'VersionStatus' => 'ACTIVE',
    ]);

    var_dump($updateResult);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage() . "\n";
}


// snippet-end:[workdocs.php.upload_document.main]
// snippet-end:[workdocs.php.upload_document.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[UploadDocument.php demonstrates how to upload a document to Amazon WorkDocs.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[InitiateDocumentVersionUploadRequest]
// snippet-keyword:[UpdateDocumentVersionRequest]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-09]
// snippet-sourceauthor:[jschwarzwalder (AWS)]