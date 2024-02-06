<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[polly.php.put_lexicon.complete]
// snippet-start:[polly.php.put_lexicon.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[polly.php.put_lexicon.import]

/**
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[polly.php.put_lexicon.main]
// Create a PollyClient
$client = new Aws\Polly\PollyClient([
    'profile' => 'default',
    'version' => '2016-06-10',
    'region' => 'us-east-2'
]);

$name = 'lexiconName';
$PLS = '
        <lexicon version="1.0" 
              xmlns="http://www.w3.org/2005/01/pronunciation-lexicon"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
              xsi:schemaLocation="http://www.w3.org/2005/01/pronunciation-lexicon 
                http://www.w3.org/TR/2007/CR-pronunciation-lexicon-20071212/pls.xsd"
              alphabet="ipa" 
              xml:lang="en-US">
                  <lexeme>
                    <grapheme>W3C</grapheme>
                    <alias>World Wide Web Consortium</alias>
                  </lexeme>
        </lexicon>';

try {
    $result = $client->putLexicon([
        'Name' => $name,
        'Content' => $PLS,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[polly.php.put_lexicon.main]
// snippet-end:[polly.php.put_lexicon.complete]
