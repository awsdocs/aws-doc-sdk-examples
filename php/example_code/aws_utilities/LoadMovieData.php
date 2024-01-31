<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AwsUtilities;

use GuzzleHttp\Client as GuzzleClient;
use GuzzleHttp\Exception\GuzzleException;
use GuzzleHttp\Psr7\Utils;
use ZipArchive;

function loadMovieData()
{
    $movieFileName = 'moviedata.json';
    $movieZipName = 'moviedata.zip';

    $url = 'https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip';
    $guzzle = new GuzzleClient(['verify' => false]);
    $file = Utils::tryFopen($movieZipName, 'w');
    try {
        $guzzle->request("get", $url, ['sink' => $file]);
    } catch (GuzzleException $e) {
        echo "Could not retrieve remote file. {$e->getCode()}: {$e->getMessage()}\n";
        return null;
    }

    $zip = new ZipArchive();
    $extractPath = ".";
    if ($zip->open($movieZipName) !== true) {
        echo "Could not open or find the zip file. Check your file system permissions.";
    }
    $zip->extractTo($extractPath);
    $zip->close();

    return file_get_contents($movieFileName);
}
