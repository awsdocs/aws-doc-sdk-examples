<?php
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.complete]
// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.import]
require './vendor/autoload.php';

use Aws\Credentials\CredentialProvider;
use Aws\Signature\SignatureV4;
use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.import]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Searches an Amazon CloudSearch domain.
 * 
 * Prerequisites: An existing Amazon CloudSearch domain.
 *
 * Inputs:
 * - $client: An initialized Guzzle HTTP API client.
 * - $domainName: The name of the domain to search.
 * - $domainId: The endpoint ID of the domain.
 * - $domainRegion: The AWS Region code for the domain.
 * - $searchString: The query string to use for the search.
 * 
 * Returns: Information about the search results; otherwise, the error.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.main] 
function searchDomain($client, $domainName, $domainId,
    $domainRegion, $searchString)
{
    $domainPrefix = 'search-';
    $cloudSearchDomain = 'cloudsearch.amazonaws.com';
    $cloudSearchVersion = '2013-01-01';
    $searchPrefix = 'search?';

    // Specify the search to send.
    $request = new Request(
        'GET',
        "https://{$domainPrefix}{$domainName}-{$domainId}.{$domainRegion}." .
            "{$cloudSearchDomain}/{$cloudSearchVersion}/" .
            "{$searchPrefix}{$searchString}"
    );

    // Get default AWS account access credentials.
    $credentials = call_user_func(CredentialProvider::defaultProvider())->wait();

    // Sign the search request with the credentials.
    $signer = new SignatureV4('cloudsearch', $domainRegion);
    $request = $signer->signRequest($request, $credentials);

    // Send the signed search request.
    $response = $client->send($request);

    // Report the search results, if any.
    $results = json_decode($response->getBody());

    $message = '';

    if ($results->hits->found > 0) {

        $message .= 'Search results:' . "\n";

        foreach($results->hits->hit as $hit)
        {
            $message .= $hit->fields->title . "\n";
        }
    } else {
        $message .= 'No search results.';
    }

    return $message;
}

function searchADomain()
{
    $domainName = 'my-search-domain';
    $domainId = '7kbitd6nyiglhdtmssxEXAMPLE';
    $domainRegion = 'us-east-1';
    $searchString = 'q=star+wars&return=title';
    $client = new Client();

    echo searchDomain($client, $domainName, $domainId, 
        $domainRegion, $searchString);
}

// Uncomment the following line to run this code in an AWS account.
// searchADomain();
// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.main]
// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.complete]
// snippet-sourcedescription:[SignDomainRequest.php demonstrates how to sign a custom Amazon CloudSearch domain request.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon CloudSearch]
// snippet-keyword:[signRequest]
// snippet-service:[cloudsearch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[jschwarzwalder (AWS)]