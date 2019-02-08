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
// snippet-start:[workdocs.php.update_document.complete]
// snippet-start:[workdocs.php.update_document.import]

require 'vendor/autoload.php';

use Aws\WorkDocs\WorkDocsClient; 
use Aws\Exception\AwsException;
// snippet-end:[workdocs.php.update_document.import]


/**
 * Update a document in Amazon WorkDocs.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a workdocs Client 
// snippet-start:[workdocs.php.update_document.main]
$client = new Aws\WorkDocs\WorkDocsClient([
    'profile' => 'default',
    'version' => '2016-05-01',
    'region' => 'us-east-2'
]);

$folder = ;
$documentID = ;

try {
    $result = $client->initiateDocumentVersionUpload([
    
         'ParentFolderId' => $folder,
         'Id' => $documentID;
    ]);
    var_dump($result);
    $documentID = $result['Metadata']['Id'];
    $documentVersionID = $result['Metadata']['LatestVersionMetadata']['Id'];
    $uploadurl = $result['UploadMetadata']['UploadUrl'];
    URL url = new URL(uploadUrl);
    
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("PUT");
    // Content-Type supplied here should match with the Content-Type set 
    // in the InitiateDocumentVersionUpload request.
    connection.setRequestProperty("Content-Type","application/octet-stream");
    connection.setRequestProperty("x-amz-server-side-encryption", "AES256");
    File file = new File("/path/to/file.txt");
    FileInputStream fileInputStream = new FileInputStream(file);
    OutputStream outputStream = connection.getOutputStream();
    com.amazonaws.util.IOUtils.copy(fileInputStream, outputStream);
    connection.getResponseCode();

} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}


// snippet-end:[workdocs.php.update_document.main]
// snippet-end:[workdocs.php.update_document.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[UpdateDocument.php demonstrates how to upload a new version of a document to Amazon WorkDocs.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[InitiateDocumentVersionUploadRequest]
// snippet-keyword:[UpdateDocumentVersionRequest]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[jschwarzwalder (AWS)]