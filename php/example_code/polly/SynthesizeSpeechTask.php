<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[polly.php.synthesize_speech_task.complete]
// snippet-start:[polly.php.synthesize_speech_task.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;

// snippet-end:[polly.php.synthesize_speech_task.import]

/**
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// snippet-start:[polly.php.synthesize_speech_task.main]
// Create a PollyClient
$client = new Aws\Polly\PollyClient([
    'profile' => 'default',
    'version' => '2016-06-10',
    'region' => 'us-east-2'
]);

$text = 'This is a sample text to be synthesized.';
$format = 'mp3'; //json|mp3|ogg_vorbis|pcm
$S3Bucket = 'bucketName';
$voice = 'Joanna';

try {
    $result = $client->startSpeechSynthesisTask([
        'Text' => $text,
        'OutputFormat' => $format,
        'OutputS3BucketName' => $S3Bucket,
        'VoiceId' => $voice,
    ]);
    $taskId = $result['SynthesisTask']['TaskId'];
    print('<p>Task started: ' . $taskId . '</p>');
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}

// snippet-end:[polly.php.synthesize_speech_task.main]
// snippet-end:[polly.php.synthesize_speech_task.complete]
