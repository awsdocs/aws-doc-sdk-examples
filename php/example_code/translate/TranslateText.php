<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html

 *
 */
// snippet-start:[translate.php.traslate_text.complete]
// snippet-start:[translate.php.traslate_text.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[translate.php.traslate_text.import]

/**
 * Translate a text from Arabic (ar), Chinese (Simplified) (zh)
 * French (fr), German (de), Portuguese (pt), or Spanish (es)
 * into English (en) with Translate client.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[translate.php.traslate_text.main]
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
$targetLanguage = 'en';

$textToTranslate =
    'El AWS SDK for PHP versión 3 permite a los desarrolladores de PHP utilizar Amazon Web Services en su código PHP 
    y crear aplicaciones y software robustos utilizando servicios como Amazon S3, Amazon DynamoDB, Amazon Glacier, etc.
     Puede empezar rápidamente instalando el SDK mediante Composer (solicitando el paquete aws/aws-sdk-php) o 
     descargando el archivo aws.zip o aws.phar independiente';

try {
    $result = $client->translateText([
        'SourceLanguageCode' => $currentLanguage,
        'TargetLanguageCode' => $targetLanguage,
        'Text' => $textToTranslate,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[translate.php.traslate_text.main]
// snippet-end:[translate.php.traslate_text.complete]
