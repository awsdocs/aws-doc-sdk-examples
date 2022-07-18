<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use GuzzleHttp\Client as GuzzleClient;

//use ZipArchive;

function loadMovieData()
{
    $movieFileName = 'moviedata.json';
    $movieZipName = 'moviedata.zip';

    $url = 'https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip';
    $guzzle = new GuzzleClient(['verify' => false]);
    $file = \GuzzleHttp\Psr7\Utils::tryFopen($movieZipName, 'w');
    $guzzle->request("get", $url, ['sink' => $file]);

    $zip = new ZipArchive();
    $extractPath = ".";
    if ($zip->open($movieZipName) !== true) {
        echo "Could not open/find the zip file. Please check your file system permissions.";
    }
    $zip->extractTo($extractPath);
    $zip->close();

    return file_get_contents($movieFileName);
}
