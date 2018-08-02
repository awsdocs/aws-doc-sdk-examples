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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the KMS Developer Guide topic at
 *  https://docs.aws.amazon.com/kms/latest/developerguide/programming-keys.html
 *
 */

require 'vendor/autoload.php';

use Aws\Kms\KmsClient;
use Aws\Exception\AwsException;

/**
 * Creating an Amazon KMS client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a KMSClient
$KmsClient = new Aws\Kms\KmsClient([
    'profile' => 'default',
    'version' => '2014-11-01',
    'region'  => 'us-east-2'
]);

$keyId = 'arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab';
$message = pack('c*',1,2,3,4,5,6,7,8,9,0);

try {
    $result = $KmsClient->encrypt([
        'KeyId' => $keyId, 
        'Plaintext' => $message,
    ]);
    var_dump($result);
}catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}