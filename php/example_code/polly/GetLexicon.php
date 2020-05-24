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
 *
 *
 *
 */
// snippet-start:[polly.php.get_lexicon.complete]
// snippet-start:[polly.php.get_lexicon.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Polly\PollyClient;

// snippet-end:[polly.php.get_lexicon.import]

/**
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[polly.php.get_lexicon.main]
// Create a PollyClient
$client = new Aws\Polly\PollyClient([
    'profile' => 'default',
    'version' => '2016-06-10',
    'region' => 'us-east-2'
]);

$name = 'lexiconName';

try {
    $result = $client->getLexicon([
        'Name' => $name,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
// snippet-end:[polly.php.get_lexicon.main]
// snippet-end:[polly.php.get_lexicon.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[Get demonstrates how to retrieve details about a specific pronunciation lexicon.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Polly]
// snippet-service:[polly]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-16]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

