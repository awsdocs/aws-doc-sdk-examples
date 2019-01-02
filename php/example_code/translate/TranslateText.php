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
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html
 *
 */
require 'vendor/autoload.php';

use Aws\Translate\TranslateClient;
use Aws\Exception\AwsException;

/**
 * Translate a text from Arabic (ar), Chinese (Simplified) (zh)
 * French (fr), German (de), Portuguese (pt), or Spanish (es) 
 * into English (en) with Translate client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create a Translate Client
$client = new Aws\Translate\TranslateClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2017-07-01'
]);


// Arabic (ar), Chinese (Simplified) (zh), English (en)
// French (fr), German (de), Portuguese (pt), or Spanish (es) 

$currentLanguage = 'es';

// If the TargetLanguageCode is not "en", the SourceLanguageCode must be "en".
$targetLanguage= 'en';


$textToTranslate = 'El AWS SDK for PHP versión 3 permite a los desarrolladores de PHP utilizar Amazon Web Services en su código PHP y crear aplicaciones y software robustos utilizando servicios como Amazon S3, Amazon DynamoDB, Amazon Glacier, etc. Puede empezar rápidamente instalando el SDK mediante Composer (solicitando el paquete aws/aws-sdk-php) o descargando el archivo aws.zip o aws.phar independiente';

try {
    $result = $client->translateText([
        'SourceLanguageCode' => $currentLanguage,
        'TargetLanguageCode' => $targetLanguage, 
        'Text' => $textToTranslate, 
    ]);
    var_dump($result);
}catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}



//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[TranslateText.php demonstrates how to translate a piece of Arabic (ar), Chinese (Simplified) (zh), English (en), French (fr), German (de), Portuguese (pt), or Spanish (es) text into English with the Translate API.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[translateText]
//snippet-keyword:[Amazon Translate]
//snippet-service:[translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-27]
//snippet-sourceauthor:[jschwarzwalder (AWS)]
