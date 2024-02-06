<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.php.object_acl.complete]
// snippet-start:[s3.php.object_acl.import]

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Aws\Exception\AwsException;
// snippet-end:[s3.php.object_acl.import]

// Create a S3Client
// snippet-start:[s3.php.object_acl.main]
$s3Client = new S3Client([
    'region' => 'us-west-2',
    'version' => '2006-03-01'
]);

// Gets the access control list (ACL) of an object.
$bucket = 'my-s3-bucket';
$key = 'my-object';
try {
    $resp = $s3Client->getObjectAcl([
        'Bucket' => $bucket,
        'Key' => $key,
    ]);
    echo "Succeed in retrieving object ACL as follows: \n";
    var_dump($resp);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// Use acl subresource to set the access control list (ACL) permissions
// for an object that already exists in a bucket
$params = [
    'ACL' => 'public-read',
    'AccessControlPolicy' => [
        // Information can be retrieved from `getObjectAcl` response
        'Grants' => [
            [
                'Grantee' => [
                    'DisplayName' => '<string>',
                    'EmailAddress' => '<string>',
                    'ID' => '<string>',
                    'Type' => 'CanonicalUser',
                    'URI' => '<string>',
                ],
                'Permission' => 'FULL_CONTROL',
            ],
        ],
        'Owner' => [
            'DisplayName' => '<string>',
            'ID' => '<string>',
        ],
    ],
    'Bucket' => $bucket,
    'Key' => $key,
];

try {
    $resp = $s3Client->putObjectAcl($params);
    echo "Succeed in setting object ACL.\n";
} catch (AwsException $e) {
    // Display error message
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[s3.php.object_acl.main]
// snippet-end:[s3.php.object_acl.complete]
